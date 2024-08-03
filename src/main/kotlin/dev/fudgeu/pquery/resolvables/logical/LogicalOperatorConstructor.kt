package dev.fudgeu.pquery.resolvables.logical

import dev.fudgeu.pquery.resolvables.basic.Resolvable

interface LogicalOperatorConstructor {
    fun construct(left: Resolvable<Boolean>, right: Resolvable<Boolean>): Resolvable<Boolean>
}