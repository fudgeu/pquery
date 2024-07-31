package dev.fudgeu.pquery.resolvables.comparison

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.Resolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableType

class EqualsResolver<T, R>(
    val left: Resolvable<T>,
    val right: Resolvable<R>,
): BooleanResolvable {
    override fun resolve(): Boolean {
        return left.resolve() == right.resolve()
    }

    class Constructor: ComparisonOperatorConstructor {
        override fun <T, R> construct(left: Resolvable<T>, right: Resolvable<R>): BooleanResolvable {
            return EqualsResolver(left, right)
        }

        override fun isValidPairing(left: ResolvableType, right: ResolvableType): Boolean {
            return true
        }
    }
}