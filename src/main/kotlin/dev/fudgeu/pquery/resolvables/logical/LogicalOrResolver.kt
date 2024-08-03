package dev.fudgeu.pquery.resolvables.logical

import dev.fudgeu.pquery.resolvables.basic.Resolvable

class LogicalOrResolver(
    private val left: Resolvable<Boolean>,
    private val right: Resolvable<Boolean>,
): Resolvable<Boolean> {
    override fun resolve(): Boolean {
        return left.resolve() || right.resolve()
    }

    class Constructor: LogicalOperatorConstructor {
        override fun construct(left: Resolvable<Boolean>, right: Resolvable<Boolean>): Resolvable<Boolean> {
            return LogicalOrResolver(left, right)
        }
    }

}