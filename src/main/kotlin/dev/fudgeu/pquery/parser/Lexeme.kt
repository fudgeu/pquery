package dev.fudgeu.pquery.parser

data class Lexeme(
    val token: Token,
    val symbolId: Int = -1,
    val at: Int,
)