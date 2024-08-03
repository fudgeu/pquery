package dev.fudgeu.pquery.resolvables.math

import dev.fudgeu.pquery.resolvables.basic.Resolvable

class AdditionResolver(
    val left: Resolvable<Double>,
    val right: Resolvable<Double>,
) : Resolvable<Double> {
    override fun resolve(): Double {
        return left.resolve() + right.resolve()
    }

    class Constructor : MathOperatorConstructor {
        override val precedence = 100
        override fun construct(left: Resolvable<Double>, right: Resolvable<Double>): Resolvable<Double> {
            return AdditionResolver(left, right)
        }
    }
}