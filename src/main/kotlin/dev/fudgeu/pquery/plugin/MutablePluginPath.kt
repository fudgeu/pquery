package dev.fudgeu.pquery.plugin

import dev.fudgeu.pquery.resolvables.basic.*

class MutablePluginPath(
    var booleanValue: BooleanResolvable? = null,
    var numberValue: NumberResolvable? = null,
    var stringValue: StringResolvable? = null,
    var listValue: Resolvable<List<Any>>? = null,
    var resolvableType: ResolvableType? = null,
    var subPaths: MutableMap<String, MutablePluginPath>? = mutableMapOf(),
) {
    fun finalize(): PluginPath {
        return PluginPath(
            booleanValue = booleanValue,
            numberValue = numberValue,
            stringValue = stringValue,
            listValue = listValue,
            resolvableType = resolvableType,
            subPaths = subPaths?.mapValues { it.value.finalize()},
        )
    }

    fun getStringList(): Resolvable<List<String>>? {
        if (resolvableType != ResolvableType.STRING_LIST) return null
        return listValue as Resolvable<List<String>>
    }

    fun getNumberList(): Resolvable<List<Double>>? {
        if (resolvableType != ResolvableType.NUMBER_LIST) return null
        return listValue as Resolvable<List<Double>>
    }
}