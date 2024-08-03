package dev.fudgeu.pquery.resolvables.math

import dev.fudgeu.pquery.resolvables.basic.Resolvable
import kotlin.math.pow

class ExponentiationResolver(
    val left: Resolvable<Double>,
    val right: Resolvable<Double>,
) : Resolvable<Double> {
    override fun resolve(): Double {
        return left.resolve().pow(right.resolve())
    }

    class Constructor : MathOperatorConstructor {
        override val precedence = 300
        override fun construct(left: Resolvable<Double>, right: Resolvable<Double>): Resolvable<Double> {
            return ExponentiationResolver(left, right)
        }

    }
}