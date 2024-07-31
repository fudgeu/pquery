package dev.fudgeu.pquery.plugin

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.NumberResolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableType
import dev.fudgeu.pquery.resolvables.basic.StringResolvable

data class PluginPath(
    val booleanValue: BooleanResolvable? = null,
    val numberValue: NumberResolvable? = null,
    val stringValue: StringResolvable? = null,
    val resolvableType: ResolvableType?,
    val subPaths: Map<String, PluginPath>,
)