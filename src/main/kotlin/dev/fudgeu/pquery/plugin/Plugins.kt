package dev.fudgeu.pquery.plugin

import dev.fudgeu.pquery.resolvables.basic.ResolvableResult
import dev.fudgeu.pquery.resolvables.comparison.ComparisonOperatorConstructor
import dev.fudgeu.pquery.resolvables.logical.LogicalOperatorConstructor
import dev.fudgeu.pquery.resolvables.math.MathOperatorConstructor

class Plugins {
    private val registeredPlugins: MutableMap<String, Plugin> = mutableMapOf()

    fun register(plugin: Plugin) {
        registeredPlugins[plugin.name] = plugin
    }

    // Helper functions
    fun isValidLogicalOperator(operator: String): Boolean {
        for (plugin in registeredPlugins.values) {
            if (plugin.isValidLogicalOperator(operator)) return true
        }
        return false
    }

    fun isValidComparisonOperator(operator: String): Boolean {
        for (plugin in registeredPlugins.values) {
            if (plugin.isValidComparisonOperator(operator)) return true
        }
        return false
    }

    fun getLogicalOperator(operator: String): LogicalOperatorConstructor? {
        for (plugin in registeredPlugins.values) {
            val constructor = plugin.getLogicalOperator(operator)
            if (constructor != null) return constructor
        }
        return null
    }

    fun getComparisonOperator(operator: String): ComparisonOperatorConstructor? {
        for (plugin in registeredPlugins.values) {
            val constructor = plugin.getComparisonOperator(operator)
            if (constructor != null) return constructor
        }
        return null
    }

    fun getMathConstructor(operator: String): MathOperatorConstructor? {
        for (plugin in registeredPlugins.values) {
            val constructor = plugin.getMathConstructor(operator)
            if (constructor != null) return constructor
        }
        return null
    }

    fun getVariable(fullPath: String): ResolvableResult? {
        for (plugin in registeredPlugins.values) {
            val variable = plugin.getVar(fullPath)
            if (variable != null) return variable
        }
        return null
    }
}