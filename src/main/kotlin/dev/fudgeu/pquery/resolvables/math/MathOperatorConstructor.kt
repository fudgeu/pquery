package dev.fudgeu.pquery.resolvables.math

import dev.fudgeu.pquery.resolvables.basic.NumberResolvable

interface MathOperatorConstructor {
    val precedence: Int
    fun construct(left: NumberResolvable, right: NumberResolvable): NumberResolvable
}