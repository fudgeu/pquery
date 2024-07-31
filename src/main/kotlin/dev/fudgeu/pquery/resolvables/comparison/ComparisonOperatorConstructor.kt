package dev.fudgeu.pquery.resolvables.comparison

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.Resolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableType

interface ComparisonOperatorConstructor {
    fun <T, R> construct(left: Resolvable<T>, right: Resolvable<R>): BooleanResolvable

    fun isValidPairing(left: ResolvableType, right: ResolvableType): Boolean
}