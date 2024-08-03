package dev.fudgeu.pquery.plugin

import dev.fudgeu.pquery.resolvables.basic.*

// TODO this class should be redone

data class PluginPath(
    val booleanValue: BooleanResolvable? = null,
    val numberValue: NumberResolvable? = null,
    val stringValue: StringResolvable? = null,
    val listValue: Resolvable<List<Any>>? = null,
    val resolvableType: ResolvableType? = null,
    val subPaths: Map<String, PluginPath>? = mapOf(),
) {
    fun getStringList(): Resolvable<List<String>>? {
        if (resolvableType != ResolvableType.STRING_LIST) return null
        return listValue as Resolvable<List<String>>
    }

    fun getNumberList(): Resolvable<List<Double>>? {
        if (resolvableType != ResolvableType.NUMBER_LIST) return null
        return listValue as Resolvable<List<Double>>
    }
}