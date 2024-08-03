package dev.fudgeu.pquery.resolvables.basic.list

import dev.fudgeu.pquery.resolvables.basic.Resolvable

class StringListResolvable {
    companion object {
        fun of (list: List<String>): Resolvable<List<String>> {
            return object : Resolvable<List<String>> {
                override fun resolve(): List<String> = list
            }
        }
    }
}