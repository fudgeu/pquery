package dev.fudgeu.pquery.resolvables.basic

interface BooleanResolvable: Resolvable<Boolean> {
    companion object {
        fun of(value: Boolean): BooleanResolvable {
            return object : BooleanResolvable {
                override fun resolve(): Boolean = value
            }
        }

        fun of(value: () -> Boolean): BooleanResolvable {
            return object : BooleanResolvable {
                override fun resolve(): Boolean = value()
            }
        }
    }
}