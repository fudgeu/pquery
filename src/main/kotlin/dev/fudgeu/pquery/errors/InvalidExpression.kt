package dev.fudgeu.pquery.errors

import dev.fudgeu.pquery.resolvables.basic.ResolvableType

class InvalidExpression(expected: ResolvableType, got: ResolvableType, at: Int) : QueryError(at, "Expected a ${expected.name} expression at $at, but got a ${got.name} expression")