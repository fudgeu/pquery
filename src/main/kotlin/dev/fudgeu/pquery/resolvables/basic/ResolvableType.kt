package dev.fudgeu.pquery.resolvables.basic

import dev.fudgeu.pquery.parser.Token

enum class ResolvableType {
    BOOLEAN, STRING, NUMBER;

    companion object {
        fun of(token: Token): ResolvableType? {
            return when (token) {
                Token.BOOLEAN_RESOLVABLE -> BOOLEAN
                Token.NUMBER_RESOLVABLE -> NUMBER
                Token.STRING_RESOLVABLE -> STRING
                else -> null
            }
        }
    }
}