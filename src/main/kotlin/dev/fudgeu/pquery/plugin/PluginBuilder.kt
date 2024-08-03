package dev.fudgeu.pquery.plugin

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