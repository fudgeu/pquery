package dev.fudgeu.pquery.parser

data class TokenizedQuery(
    val lexemes: List<Lexeme>,
    val symbols: SymbolTable,
)