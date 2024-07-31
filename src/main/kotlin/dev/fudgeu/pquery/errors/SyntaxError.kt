package dev.fudgeu.pquery.errors

class SyntaxError(override val message: String, override val at: Int) : QueryError(at, "[:$at] $message")