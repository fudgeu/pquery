package dev.fudgeu.pquery.errors

open class QueryError(open val at: Int, override val message: String) : Exception(message)