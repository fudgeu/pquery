package dev.fudgeu.pquery.resolvables.math

import dev.fudgeu.pquery.resolvables.basic.NumberResolvable
import kotlin.math.pow

class ExponentiationResolver(
    val left: NumberResolvable,
    val right: NumberResolvable,
) : NumberResolvable {
    override fun resolve(): Double {
        return left.resolve().pow(right.resolve())
    }

    class Constructor : MathOperatorConstructor {
        override val precedence = 300
        override fun construct(left: NumberResolvable, right: NumberResolvable): NumberResolvable {
            return ExponentiationResolver(left, right)
        }

    }
}