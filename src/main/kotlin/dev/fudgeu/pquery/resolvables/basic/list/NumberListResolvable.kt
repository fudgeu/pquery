package dev.fudgeu.pquery.resolvables.basic.list

import dev.fudgeu.pquery.resolvables.basic.Resolvable

class NumberListResolvable {
    companion object {
        fun of (list: List<Double>): Resolvable<List<Double>> {
            return object : Resolvable<List<Double>> {
                override fun resolve(): List<Double> = list
            }
        }
    }
}