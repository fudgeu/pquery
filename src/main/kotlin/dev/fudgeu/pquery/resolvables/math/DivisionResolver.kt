package dev.fudgeu.pquery.resolvables.math

import dev.fudgeu.pquery.resolvables.basic.NumberResolvable

class DivisionResolver(
    val left: NumberResolvable,
    val right: NumberResolvable,
) : NumberResolvable {
    override fun resolve(): Double {
        return left.resolve() / right.resolve()
    }

    class Constructor : MathOperatorConstructor {
        override val precedence = 200
        override fun construct(left: NumberResolvable, right: NumberResolvable): NumberResolvable {
            return DivisionResolver(left, right)
        }

    }
}