package dev.fudgeu.pquery.resolvables.basic

import dev.fudgeu.pquery.parser.Token

enum class ResolvableType {
    BOOLEAN, STRING, NUMBER, STRING_LIST, NUMBER_LIST;

    companion object {
        fun of(token: Token): ResolvableType? {
            return when (token) {
                Token.BOOLEAN_RESOLVABLE -> BOOLEAN
                Token.NUMBER_RESOLVABLE -> NUMBER
                Token.STRING_RESOLVABLE -> STRING
                Token.STRING_LIST_RESOLVABLE -> STRING_LIST
                Token.NUMBER_LIST_RESOLVABLE -> NUMBER_LIST
                else -> null
            }
        }
    }
}