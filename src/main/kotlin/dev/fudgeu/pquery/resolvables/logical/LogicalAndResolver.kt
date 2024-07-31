package dev.fudgeu.pquery.resolvables.logical

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable

class LogicalAndResolver(
    private val left: BooleanResolvable,
    private val right: BooleanResolvable,
): BooleanResolvable {
    override fun resolve(): Boolean {
        return left.resolve() && right.resolve()
    }

    class Constructor: LogicalOperatorConstructor {
        override fun construct(left: BooleanResolvable, right: BooleanResolvable): BooleanResolvable {
            return LogicalAndResolver(left, right)
        }

    }
}