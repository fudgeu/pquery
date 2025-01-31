package dev.fudgeu.pquery.plugin

import dev.fudgeu.pquery.resolvables.basic.*
import dev.fudgeu.pquery.resolvables.basic.list.NumberListResolvable
import dev.fudgeu.pquery.resolvables.basic.list.StringListResolvable
import dev.fudgeu.pquery.resolvables.comparison.ComparisonOperatorConstructor
import dev.fudgeu.pquery.resolvables.logical.LogicalOperatorConstructor
import dev.fudgeu.pquery.resolvables.math.MathOperatorConstructor

open class Plugin(
    val name: String,
    private val logicalOperators: Map<String, LogicalOperatorConstructor> = emptyMap(),
    private val comparisonOperators: Map<String, ComparisonOperatorConstructor> = emptyMap(),
    private val mathOperators: Map<String, MathOperatorConstructor> = emptyMap(),
    variables: Map<String, PluginPath> = emptyMap(),
) {
    private val variableTypes: Map<String, ResolvableType>
    private val booleanVars: Map<String, BooleanResolvable>
    private val numberVars: Map<String, NumberResolvable>
    private val stringVars: Map<String, StringResolvable>
    private val stringListVars: Map<String, Resolvable<List<String>>>
    private val numberListVars: Map<String, Resolvable<List<Double>>>

    init {
        // Compile variables into a few hashmaps
        val types = mutableMapOf<String, ResolvableType>()
        val bools = mutableMapOf<String, BooleanResolvable>()
        val numbers = mutableMapOf<String, NumberResolvable>()
        val strings = mutableMapOf<String, StringResolvable>()
        val stringLists = mutableMapOf<String, Resolvable<List<String>>>()
        val numberLists = mutableMapOf<String, Resolvable<List<Double>>>()

        val queue = ArrayDeque<Pair<String, Map<String, PluginPath>?>>()
        queue.add(Pair(name, variables))
        while (queue.isNotEmpty()) {
            val thisPath = queue.removeFirst()
            val childPaths = thisPath.second ?: continue
            for ((key, value) in childPaths) {
                val absolutePath = thisPath.first.plus(".$key")
                if (value.resolvableType != null) types[absolutePath] = value.resolvableType

                // Set value
                when (value.resolvableType) {
                    ResolvableType.BOOLEAN ->
                        bools[absolutePath] = value.booleanValue ?: BooleanResolvable.of(false)
                    ResolvableType.NUMBER ->
                        numbers[absolutePath] = value.numberValue ?: NumberResolvable.of(0)
                    ResolvableType.STRING ->
                        strings[absolutePath] = value.stringValue ?: StringResolvable.of("")
                    ResolvableType.STRING_LIST ->
                        stringLists[absolutePath] = value.getStringList() ?: StringListResolvable.of(emptyList())
                    ResolvableType.NUMBER_LIST ->
                        numberLists[absolutePath] = value.getNumberList() ?: NumberListResolvable.of(emptyList())
                    null -> {}
                }

                // Add child paths to queue
                queue.add(Pair(absolutePath, value.subPaths))
            }
        }

        variableTypes = types
        booleanVars = bools
        numberVars = numbers
        stringVars = strings
        stringListVars = stringLists
        numberListVars = numberLists
    }
    // Helper function
    fun isValidLogicalOperator(operator: String): Boolean {
        return logicalOperators.containsKey(operator)
    }

    fun isValidComparisonOperator(operator: String): Boolean {
        return comparisonOperators.containsKey(operator)
    }

    fun getLogicalOperator(operator: String): LogicalOperatorConstructor? {
        return logicalOperators[operator]
    }

    fun getComparisonOperator(operator: String): ComparisonOperatorConstructor? {
        return comparisonOperators[operator]
    }

    fun getMathConstructor(operator: String): MathOperatorConstructor? {
        return mathOperators[operator]
    }

    fun getBooleanVar(fullPath: String): BooleanResolvable? {
        return booleanVars[fullPath]
    }

    fun getNumberVar(fullPath: String): NumberResolvable? {
        return numberVars[fullPath]
    }

    fun getStringVar(fullPath: String): StringResolvable? {
        return stringVars[fullPath]
    }

    fun getStringListVar(fullPath: String): Resolvable<List<String>>? {
        return stringListVars[fullPath]
    }

    fun getNumberListVar(fullPath: String): Resolvable<List<Double>>? {
        return numberListVars[fullPath]
    }

    fun getVar(fullPath: String): ResolvableResult? {
        return when (variableTypes[fullPath]) {
            ResolvableType.BOOLEAN ->
                booleanVars[fullPath]?.let { ResolvableResult.of(it) }
            ResolvableType.NUMBER ->
                numberVars[fullPath]?.let { ResolvableResult.of(it) }
            ResolvableType.STRING ->
                stringVars[fullPath]?.let { ResolvableResult.of(it) }
            ResolvableType.STRING_LIST ->
                stringListVars[fullPath]?.let { ResolvableResult.of(it) }
            ResolvableType.NUMBER_LIST ->
                numberListVars[fullPath]?.let { ResolvableResult.of(it) }
            null -> null
        }
    }

}