package dev.fudgeu.pquery.plugin

import dev.fudgeu.pquery.resolvables.basic.*
import dev.fudgeu.pquery.resolvables.comparison.ComparisonOperatorConstructor
import dev.fudgeu.pquery.resolvables.logical.LogicalOperatorConstructor
import dev.fudgeu.pquery.resolvables.math.MathOperatorConstructor

class Plugin(
    val name: String,
    private val logicalOperators: Map<String, LogicalOperatorConstructor>,
    private val comparisonOperators: Map<String, ComparisonOperatorConstructor>,
    private val mathOperators: Map<String, MathOperatorConstructor>,
    variables: Map<String, PluginPath>,
) {
    private val variableTypes: Map<String, ResolvableType>
    private val booleanVars: Map<String, BooleanResolvable>
    private val numberVars: Map<String, NumberResolvable>
    private val stringVars: Map<String, StringResolvable>

    init {
        // Compile variables into a few hashmaps
        val types = mutableMapOf<String, ResolvableType>()
        val bools = mutableMapOf<String, BooleanResolvable>()
        val numbers = mutableMapOf<String, NumberResolvable>()
        val strings = mutableMapOf<String, StringResolvable>()

        val queue = ArrayDeque<Pair<String, Map<String, PluginPath>>>()
        queue.add(Pair(name, variables))
        while (queue.isNotEmpty()) {
            val thisPath = queue.removeFirst()
            for ((key, value) in thisPath.second) {
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
        return getNumberVar(fullPath)
    }

    fun getStringVar(fullPath: String): StringResolvable? {
        return getStringVar(fullPath)
    }

    fun getVar(fullPath: String): ResolvableResult? {
        return when (variableTypes[fullPath]) {
            ResolvableType.BOOLEAN ->
                booleanVars[fullPath]?.let { ResolvableResult(ResolvableType.BOOLEAN, boolean = it) }
            ResolvableType.NUMBER ->
                numberVars[fullPath]?.let { ResolvableResult(ResolvableType.NUMBER, number = it) }
            ResolvableType.STRING ->
                stringVars[fullPath]?.let { ResolvableResult(ResolvableType.STRING, string = it) }
            null -> null
        }
    }

}