package dev.fudgeu.pquery.errors

class InvalidToken(val token: String, override val at: Int) : QueryError(at, "Invalid token '$token' with at $at")