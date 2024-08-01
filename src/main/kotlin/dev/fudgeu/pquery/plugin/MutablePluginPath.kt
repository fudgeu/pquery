package dev.fudgeu.pquery.plugin

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.NumberResolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableType
import dev.fudgeu.pquery.resolvables.basic.StringResolvable

class MutablePluginPath(
    var booleanValue: BooleanResolvable? = null,
    var numberValue: NumberResolvable? = null,
    var stringValue: StringResolvable? = null,
    var resolvableType: ResolvableType? = null,
    var subPaths: MutableMap<String, MutablePluginPath>? = mutableMapOf(),
) {
    fun finalize(): PluginPath {
        return PluginPath(
            booleanValue = booleanValue,
            numberValue = numberValue,
            stringValue = stringValue,
            resolvableType = resolvableType,
            subPaths = subPaths?.mapValues { it.value.finalize()},
        )
    }
}