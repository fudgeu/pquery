package dev.fudgeu.pquery.resolvables.math

import dev.fudgeu.pquery.resolvables.basic.Resolvable

interface MathOperatorConstructor {
    val precedence: Int
    fun construct(left: Resolvable<Double>, right: Resolvable<Double>): Resolvable<Double>
}