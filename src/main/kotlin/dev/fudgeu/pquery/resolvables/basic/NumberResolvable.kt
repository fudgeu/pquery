package dev.fudgeu.pquery.resolvables.basic

interface NumberResolvable: Resolvable<Double> {
    companion object {
        fun of(value: Double): NumberResolvable {
            return object : NumberResolvable {
                override fun resolve(): Double = value
            }
        }
        fun of(value: Int): NumberResolvable {
            val doubleRepresentation = value.toDouble()
            return object : NumberResolvable {
                override fun resolve(): Double = doubleRepresentation
            }
        }
        fun of(value: Float): NumberResolvable {
            val doubleRepresentation = value.toDouble()
            return object : NumberResolvable {
                override fun resolve(): Double = doubleRepresentation
            }
        }
    }
}
