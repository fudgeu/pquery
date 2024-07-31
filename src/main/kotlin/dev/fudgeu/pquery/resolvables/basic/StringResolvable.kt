package dev.fudgeu.pquery.resolvables.basic

interface StringResolvable: Resolvable<String> {
    companion object {
        fun of(value: String): StringResolvable {
            return object : StringResolvable {
                override fun resolve(): String = value
            }
        }
    }
}