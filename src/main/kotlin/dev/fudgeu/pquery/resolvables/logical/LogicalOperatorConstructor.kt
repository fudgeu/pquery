package dev.fudgeu.pquery.resolvables.logical

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable

interface LogicalOperatorConstructor {
    fun construct(left: BooleanResolvable, right: BooleanResolvable): BooleanResolvable
}