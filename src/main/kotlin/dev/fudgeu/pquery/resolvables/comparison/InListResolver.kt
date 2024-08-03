package dev.fudgeu.pquery.resolvables.comparison

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.Resolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableType

class InListResolver<T>(
    val left: Resolvable<T>,
    val right: Resolvable<List<T>>
) : BooleanResolvable {
    override fun resolve(): Boolean {
        return right.resolve().contains(left.resolve())
    }

    class Constructor : ComparisonOperatorConstructor {
        override fun <T, R> construct(left: Resolvable<T>, right: Resolvable<R>): BooleanResolvable {
            return InListResolver(left, right as Resolvable<List<T>>)
        }

        override fun isValidPairing(left: ResolvableType, right: ResolvableType): Boolean {
            return if (left == ResolvableType.STRING && right == ResolvableType.STRING_LIST) true
            else if (left == ResolvableType.NUMBER && right == ResolvableType.NUMBER_LIST) true
            else false
        }

    }

}