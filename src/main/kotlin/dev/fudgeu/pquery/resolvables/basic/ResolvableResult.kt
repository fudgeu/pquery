package dev.fudgeu.pquery.resolvables.basic

data class ResolvableResult(
    val type: ResolvableType,
    val boolean: BooleanResolvable? = null,
    val number: NumberResolvable? = null,
    val string: StringResolvable? = null,
)