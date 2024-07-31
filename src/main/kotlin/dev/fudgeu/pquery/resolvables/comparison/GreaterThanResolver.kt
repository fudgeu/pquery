package dev.fudgeu.pquery.resolvables.comparison

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.Resolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableType

class GreaterThanResolver(
    val left: Resolvable<Double>,
    val right: Resolvable<Double>,
): BooleanResolvable {
    override fun resolve(): Boolean {
        return left.resolve() > right.resolve()
    }

    class Constructor: ComparisonOperatorConstructor {
        override fun <T, R> construct(left: Resolvable<T>, right: Resolvable<R>): BooleanResolvable {
            return GreaterThanResolver(left as Resolvable<Double>, right as Resolvable<Double>)
        }

        override fun isValidPairing(left: ResolvableType, right: ResolvableType): Boolean {
            return left == ResolvableType.NUMBER && right == ResolvableType.NUMBER
        }
    }
}