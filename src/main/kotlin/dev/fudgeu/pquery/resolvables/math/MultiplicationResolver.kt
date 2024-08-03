package dev.fudgeu.pquery.resolvables.math

import dev.fudgeu.pquery.resolvables.basic.Resolvable

class MultiplicationResolver(
    val left: Resolvable<Double>,
    val right: Resolvable<Double>,
) : Resolvable<Double> {
    override fun resolve(): Double {
        return left.resolve() * right.resolve()
    }

    class Constructor : MathOperatorConstructor {
        override val precedence = 200
        override fun construct(left: Resolvable<Double>, right: Resolvable<Double>): Resolvable<Double> {
            return MultiplicationResolver(left, right)
        }

    }
}