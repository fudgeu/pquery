package dev.fudgeu.pquery.plugin

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import dev.fudgeu.pquery.errors.PluginBuilderError
import dev.fudgeu.pquery.resolvables.basic.*
import dev.fudgeu.pquery.resolvables.basic.list.NumberListResolvable
import dev.fudgeu.pquery.resolvables.basic.list.StringListResolvable
import dev.fudgeu.pquery.resolvables.comparison.ComparisonOperatorConstructor
import dev.fudgeu.pquery.resolvables.logical.LogicalOperatorConstructor
import dev.fudgeu.pquery.resolvables.math.MathOperatorConstructor

class PluginBuilder(val name: String) {

    private val logicalConstructors: MutableMap<String, LogicalOperatorConstructor> = mutableMapOf()
    private val comparisonConstructors: MutableMap<String, ComparisonOperatorConstructor> = mutableMapOf()
    private val mathConstructors: MutableMap<String, MathOperatorConstructor> = mutableMapOf()
    private val variables: MutableMap<String, MutablePluginPath> = mutableMapOf()

    fun registerLogicalOperator(operator: String, constructor: LogicalOperatorConstructor) {
        logicalConstructors[operator] = constructor
    }

    fun registerComparisonOperator(operator: String, constructor: ComparisonOperatorConstructor) {
        comparisonConstructors[operator] = constructor
    }

    fun registerMathematicalOperator(operator: String, constructor: MathOperatorConstructor) {
        mathConstructors[operator] = constructor
    }

    fun registerBoolean(path: String, value: Boolean) {
        registerBoolean(path, BooleanResolvable.of(value))
    }

    fun registerBoolean(path: String, value: BooleanResolvable) {
        val pluginPath = getPath(path)
        pluginPath.resolvableType = ResolvableType.BOOLEAN
        pluginPath.booleanValue = value
    }

    fun registerNumber(path: String, value: Double) {
        registerNumber(path, NumberResolvable.of(value))
    }

    fun registerNumber(path: String, value: NumberResolvable) {
        val pluginPath = getPath(path)
        pluginPath.resolvableType = ResolvableType.NUMBER
        pluginPath.numberValue = value
    }

    fun registerString(path: String, value: String) {
        registerString(path, StringResolvable.of(value))
    }

    fun registerString(path: String, value: StringResolvable) {
        val pluginPath = getPath(path)
        pluginPath.resolvableType = ResolvableType.STRING
        pluginPath.stringValue = value
    }

    fun registerStringList(path: String, value: List<String>) {
        registerStringList(path, StringListResolvable.of(value))
    }

    fun registerStringList(path: String, value: Resolvable<List<String>>) {
        val pluginPath = getPath(path)
        pluginPath.resolvableType = ResolvableType.STRING_LIST
        pluginPath.listValue = value
    }

    fun registerNumberList(path: String, value: List<Double>) {
        registerNumberList(path, NumberListResolvable.of(value))
    }

    fun registerNumberList(path: String, value: Resolvable<List<Double>>) {
        val pluginPath = getPath(path)
        pluginPath.resolvableType = ResolvableType.NUMBER_LIST
        pluginPath.listValue = value
    }

    fun build(): Plugin {
        return Plugin(
            name,
            logicalConstructors,
            comparisonConstructors,
            mathConstructors,
            variables.mapValues { it.value.finalize() }
        )
    }

    companion object {
        fun fromJson(rawJson: String): PluginBuilder {
            // Read JSON
            val rootObj = JsonParser.parseString(rawJson).asJsonObject

            // Initialize plugin builder
            val plugin = PluginBuilder(rootObj.get("name").asString)

            // Build list of variables
            val initialItems = rootObj.get("variables").asJsonObject.asMap()
            val queue = ArrayDeque<Pair<String, JsonElement>>()
            queue.addAll(initialItems.map { Pair(it.key, it.value) }.reversed() )
            while (queue.isNotEmpty()) {
                val (path, value) = queue.removeLast() // Go DFS instead of BFS

                // Proceed down list if object
                if (value.isJsonObject) {
                    val newItems = value.asJsonObject.asMap()
                        .map { Pair("$path.${it.key}", it.value) }
                        .reversed()
                    queue.addAll(newItems)
                    continue
                }

                // Process if list
                if(value.isJsonArray) {
                    val list = value.asJsonArray.asList()

                    // Ensure all list elements are the same type, add it to a list
                    val processedList = mutableListOf<Any>()
                    var listType: ResolvableType? = null
                    for (item in list) {
                        val primItem = item.asJsonPrimitive
                        val type = if (primItem.isNumber) ResolvableType.NUMBER
                            else if (primItem.isString) ResolvableType.STRING
                            else throw PluginBuilderError("Found invalid list item - all list items must be either as string or number.")

                        if (listType == null) {
                            listType = type
                        } else if (type != listType){
                            throw PluginBuilderError("Found inconsistent list item - all list items must be of the same type.")
                        }

                        processedList.add(item)
                    }

                    // Register list in plugin
                    if (listType == ResolvableType.STRING)
                        plugin.registerStringList(path, processedList as List<String>)
                    else if (listType == ResolvableType.NUMBER)
                        plugin.registerNumberList(path, processedList as List<Double>)

                    continue
                }

                // Add primitive
                val fixedPath = if (path.endsWith("..")) { // Catch "." self values in trees
                    path.substring(0, path.length - 2)
                } else path

                val primObj = value.asJsonPrimitive
                if (primObj.isString) {
                    plugin.registerString(fixedPath, primObj.asString)
                } else if (primObj.isNumber) {
                    plugin.registerNumber(fixedPath, primObj.asDouble)
                } else if (primObj.isBoolean) {
                    plugin.registerBoolean(fixedPath, primObj.asBoolean)
                } else {
                    throw PluginBuilderError("Unsupported JSON element type at $fixedPath (${primObj})")
                }
            }

            return plugin
        }
    }

    // Will grab the plugin path specified, creating it (recursively) if it does not exist
    private fun getPath(path: String): MutablePluginPath {
        val splitPath = path.split('.').toMutableList()
        if (splitPath.getOrNull(0) == name) splitPath.removeFirst()
        if (splitPath.isEmpty()) throw Throwable("Fix later")

        var curPath: MutablePluginPath? = null
        var curPathChildren: MutableMap<String, MutablePluginPath>? = variables
        for (pathPart in splitPath) {
            curPath = curPathChildren?.getOrPut(pathPart) { MutablePluginPath() }
            curPathChildren = curPath?.subPaths
        }

        if (curPath == null) throw Throwable("Technically impossible")
        return curPath
    }

}