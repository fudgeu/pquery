package dev.fudgeu.pquery.resolvables.basic

class ResolvableResult {
    val type: ResolvableType
    val value: Resolvable<Any>

    private constructor(type: ResolvableType, value: Resolvable<Any>) {
        this.type = type
        this.value = value
    }

    companion object {
        @JvmName("ofBoolean")
        fun of(value: Resolvable<Boolean>): ResolvableResult {
            return ResolvableResult(ResolvableType.BOOLEAN, value)
        }

        @JvmName("ofNumber")
        fun of(value: Resolvable<Double>): ResolvableResult {
            return ResolvableResult(ResolvableType.NUMBER, value)
        }

        @JvmName("ofString")
        fun of(value: Resolvable<String>): ResolvableResult {
            return ResolvableResult(ResolvableType.STRING, value)
        }

        @JvmName("ofNumberList")
        fun of(value: Resolvable<List<Number>>): ResolvableResult {
            return ResolvableResult(ResolvableType.NUMBER_LIST, value)
        }

        @JvmName("ofStringList")
        fun of(value: Resolvable<List<String>>): ResolvableResult {
            return ResolvableResult(ResolvableType.STRING_LIST, value)
        }
    }
}