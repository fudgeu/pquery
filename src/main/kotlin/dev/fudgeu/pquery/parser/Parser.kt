package dev.fudgeu.pquery.parser

import dev.fudgeu.pquery.errors.InternalFailure
import dev.fudgeu.pquery.errors.InvalidExpression
import dev.fudgeu.pquery.errors.SyntaxError
import dev.fudgeu.pquery.plugin.Plugins
import dev.fudgeu.pquery.resolvables.basic.Resolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableResult
import dev.fudgeu.pquery.resolvables.basic.ResolvableType
import dev.fudgeu.pquery.resolvables.basic.list.NumberListResolvable
import dev.fudgeu.pquery.resolvables.basic.list.StringListResolvable

class Parser(
    val tokenizedQuery: TokenizedQuery,
    val plugins: Plugins,
) {

    private var workingQuery = tokenizedQuery.lexemes.toMutableList()
    private val workingSymbolTable = SymbolTable(tokenizedQuery.symbols)
    private var index = 0;

    fun parse(): Result<Resolvable<Boolean>> {
        collapseLists()
        val resolvableResult = parseExpression()
            .onFailure { return Result.failure(it) }
            .getOrThrow()
        if (resolvableResult.type != ResolvableType.BOOLEAN)
            return Result.failure(InvalidExpression(ResolvableType.BOOLEAN, resolvableResult.type, 0))
        return Result.success(resolvableResult.value as Resolvable<Boolean>)
    }

    private fun collapseGroups(): Result<Unit> {
        val originalIndex = index

        // Keep looping through the list until we hit a right parenthesis or the end of the string
        while (workingQuery.getOrNull(index)?.token != Token.RIGHT_PARENTHESIS && workingQuery.getOrNull(index) != null) {
            val lexeme = workingQuery[index]

            // Check if we've hit a left parenthesis - process that group as an expression.
            if (lexeme.token == Token.LEFT_PARENTHESIS) {
                val groupStart = index
                index += 1

                // Process group as an expression
                val expression = parseExpression()
                    .onFailure { return Result.failure(it) }
                    .getOrThrow()

                // Validate group is closed off
                if (workingQuery.getOrNull(index)?.token != Token.RIGHT_PARENTHESIS) {
                    return Result.failure(SyntaxError("Group is missing closing parenthesis", lexeme.at))
                }
                workingQuery.removeAt(index) // Remove right paren from query

                // Add to symbol table
                val lexeme = when (expression.type) {
                    ResolvableType.BOOLEAN -> {
                        val id = workingSymbolTable.addBooleanResolvable(expression.value as Resolvable<Boolean>)
                        Lexeme(Token.BOOLEAN_RESOLVABLE, id, lexeme.at)
                    }
                    ResolvableType.NUMBER -> {
                        val id = workingSymbolTable.addNumberResolvable(expression.value as Resolvable<Double>)
                        Lexeme(Token.NUMBER_RESOLVABLE, id, lexeme.at)
                    }
                    ResolvableType.STRING -> {
                        val id = workingSymbolTable.addStringResolvable(expression.value as Resolvable<String>)
                        Lexeme(Token.STRING_RESOLVABLE, id, lexeme.at)
                    }
                    ResolvableType.STRING_LIST -> {
                        val id = workingSymbolTable.addStringListResolvable(expression.value as Resolvable<List<String>>)
                        Lexeme(Token.STRING_LIST_RESOLVABLE, id, lexeme.at)
                    }
                    ResolvableType.NUMBER_LIST -> {
                        val id = workingSymbolTable.addNumberListResolvable(expression.value as Resolvable<List<Double>>)
                        Lexeme(Token.NUMBER_LIST_RESOLVABLE, id, lexeme.at)
                    }
                }

                // Replace this group with the expression
                val newQuery = mutableListOf<Lexeme>()
                newQuery.addAll(workingQuery.subList(0, groupStart))
                newQuery.add(lexeme)
                newQuery.addAll(workingQuery.subList(index, workingQuery.size))
                workingQuery = newQuery

                index = groupStart
            }

            // Ignore other symbols, as we're only looking for groups.
            index++
        }

        index = originalIndex
        return Result.success(Unit)
    }

    private fun collapseMath(): Result<Unit> {
        val originalIndex = index

        for (precedence in workingSymbolTable.getSetOfPrecedences().reversed()) {
            while (workingQuery.getOrNull(index + 2)?.token != Token.RIGHT_PARENTHESIS && workingQuery.getOrNull(index + 2) != null) {
                val lexeme1 = workingQuery[index + 0]
                val lexeme2 = workingQuery[index + 1]
                val lexeme3 = workingQuery[index + 2]

                // Check to see if lexemes match math pattern
                if (lexeme1.token != Token.NUMBER_RESOLVABLE || lexeme2.token != Token.MATH_OPERATOR || lexeme3.token != Token.NUMBER_RESOLVABLE) {
                    index++
                    continue
                }

                // Check if we are currently processing this precedence level
                if (workingSymbolTable.getMathConstructor(lexeme2.symbolId)?.precedence != precedence) {
                    index++
                    continue
                }

                // Collapse into one resolvable
                val left = workingSymbolTable.getNumberResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())
                val right = workingSymbolTable.getNumberResolvable(lexeme3.symbolId)
                    ?: return Result.failure(InternalFailure())

                val mathConstructor = workingSymbolTable.getMathConstructor(lexeme2.symbolId)
                    ?: return Result.failure(InternalFailure())
                val mathResolvable = mathConstructor.construct(left, right)

                val newSymbolId = workingSymbolTable.addNumberResolvable(mathResolvable)

                // Replace lexemes
                val newQuery = mutableListOf<Lexeme>()
                newQuery.addAll(workingQuery.subList(0, index))
                newQuery.add(Lexeme(Token.NUMBER_RESOLVABLE, newSymbolId, lexeme1.at))
                newQuery.addAll(workingQuery.subList(index + 3, workingQuery.size))
                workingQuery = newQuery
            }

            index = originalIndex
        }

        return Result.success(Unit)
    }

    private fun collapseLists(): Result<Unit> {
        while (workingQuery.getOrNull(index) != null) {
            val lexeme = workingQuery[index]

            // Check if we've hit a left bracket
            if (lexeme.token == Token.LEFT_SQUARE_BRACKET) {
                val originalIndex = index
                val list: MutableList<Resolvable<Any>> = mutableListOf()
                var listType: ResolvableType? = null
                index++

                while (workingQuery.getOrNull(index)?.token != Token.RIGHT_SQUARE_BRACKET && workingQuery.getOrNull(index) != null) {
                    val expression = parseExpression().getOrElse { return Result.failure(it) }

                    // Make sure list item type is consistent
                    if (listType == null) {
                        listType = expression.type
                    } else if (expression.type != listType) {
                        return Result.failure(SyntaxError("List's elements must be all of the same type", index))
                    }

                    // Add item to list
                    list.add(expression.value)

                    // Expect and skip over comma, or end of list
                    val curToken = workingQuery.getOrNull(index)?.token
                    if (curToken == Token.COMMA) {
                        index++
                    } else if (curToken == Token.RIGHT_SQUARE_BRACKET || curToken == null) {
                        break
                    } else {
                        return Result.failure(SyntaxError("Expected comma", index))
                    }
                }

                // Create list, register it in symbol table
                val newLexeme = when (listType) {
                    ResolvableType.NUMBER -> {
                        val id = workingSymbolTable.addNumberListResolvable(NumberListResolvable.of( list.map { it.resolve() as Double }))
                        Lexeme(Token.NUMBER_LIST_RESOLVABLE, id, originalIndex)
                    }
                    ResolvableType.STRING -> {
                        val id = workingSymbolTable.addStringListResolvable(StringListResolvable.of( list.map { it.resolve() as String }))
                        Lexeme(Token.STRING_LIST_RESOLVABLE, id, originalIndex)
                    }
                    else -> return Result.failure(SyntaxError("Unsupported list type", originalIndex))
                }

                // Replace tokens
                val newQuery = mutableListOf<Lexeme>()
                newQuery.addAll(workingQuery.subList(0, originalIndex))
                newQuery.add(newLexeme)
                newQuery.addAll(workingQuery.subList(index + 1, workingQuery.size))
                workingQuery = newQuery

                index = originalIndex + 1
            } else {
                index++
            }
        }
        index = 0

        return Result.success(Unit)
    }

    private fun parseExpression(): Result<ResolvableResult> {
        // Collapse all groups
        collapseGroups()
            .onFailure { return Result.failure(it) }

        collapseMath() // This function will run through the tokens. It will find / and * first, and group those into a number resolvable, replacing the original tokens. Then it'll go through again, this time searching for + and -. Rinse and repeat.

        val lexeme1 = workingQuery[index + 0]
        val lexeme2 = workingQuery.getOrNull(index + 1)
        val lexeme3 = workingQuery.getOrNull(index + 2)

        // The scope should always be either 1 token (single literal or variable), or 3 tokens (a comparison or logical operation)
        // Logical operator
        if (lexeme2?.token == Token.LOGICAL_OPERATOR && lexeme3 != null) {
            // Verify both sides are boolean resolvers
            if (lexeme1.token != Token.BOOLEAN_RESOLVABLE || lexeme3.token != Token.BOOLEAN_RESOLVABLE) {
                return Result.failure(SyntaxError("Logical operator requires a boolean on each side", lexeme2.at))
            }

            val left = workingSymbolTable.getBooleanResolvable(lexeme1.symbolId)
                ?: return Result.failure(InternalFailure())
            val right = workingSymbolTable.getBooleanResolvable(lexeme3.symbolId)
                ?: return Result.failure(InternalFailure())

            val result = workingSymbolTable.getLogicalOperator(lexeme2.symbolId)?.construct(left, right)
                ?: return Result.failure(InternalFailure())
            index += 3
            return Result.success(
                ResolvableResult.of(result)
            )
        }
        // Comparison operator
        else if (lexeme2?.token == Token.COMPARISON_OPERATOR && lexeme3 != null) {
            // Verify both sides have a resolvable value
            val leftType = ResolvableType.of(lexeme1.token)
            val rightType = ResolvableType.of(lexeme3.token)
            if (leftType == null || rightType == null) {
                return Result.failure(
                    SyntaxError(
                        "Comparison operator requires a resolvable value on each size",
                        lexeme2.at
                    )
                )
            }

            val left = when (leftType) {
                ResolvableType.BOOLEAN -> workingSymbolTable.getBooleanResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.NUMBER -> workingSymbolTable.getNumberResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.STRING -> workingSymbolTable.getStringResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.NUMBER_LIST -> workingSymbolTable.getNumberListResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.STRING_LIST -> workingSymbolTable.getStringListResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())
            }
            val right = when (rightType) {
                ResolvableType.BOOLEAN -> workingSymbolTable.getBooleanResolvable(lexeme3.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.NUMBER -> workingSymbolTable.getNumberResolvable(lexeme3.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.STRING -> workingSymbolTable.getStringResolvable(lexeme3.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.NUMBER_LIST -> workingSymbolTable.getNumberListResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())

                ResolvableType.STRING_LIST -> workingSymbolTable.getStringListResolvable(lexeme1.symbolId)
                    ?: return Result.failure(InternalFailure())
            }

            val constructor = workingSymbolTable.getComparisonOperator(lexeme2.symbolId)
                ?: return Result.failure(InternalFailure())
            if (!constructor.isValidPairing(leftType, rightType))
                return Result.failure(
                    SyntaxError(
                        "Comparator does not support this combination of resolvables",
                        lexeme2.at
                    )
                )
            val result = constructor.construct(left, right)

            index += 3
            return Result.success(ResolvableResult.of(result))
        }
        else {
            if (!isResolvable(lexeme1.token)) {
                return Result.failure(SyntaxError("Expected expression", lexeme1.at))
            }
            index += 1
            return getResolvable(lexeme1)
        }

        return Result.failure(Throwable("unknown error"))
    }

    private fun isResolvable(token: Token): Boolean {
        return token == Token.BOOLEAN_RESOLVABLE || token == Token.NUMBER_RESOLVABLE || token == Token.STRING_RESOLVABLE || token == Token.NUMBER_LIST_RESOLVABLE || token == Token.STRING_LIST_RESOLVABLE
    }

    private fun getResolvable(lexeme: Lexeme): Result<ResolvableResult> {
        when (lexeme.token) {
            Token.BOOLEAN_RESOLVABLE -> {
                val resolvable = workingSymbolTable.getBooleanResolvable(lexeme.symbolId)
                    ?: return Result.failure(InternalFailure())
                return Result.success(ResolvableResult.of(resolvable))
            }
            Token.NUMBER_RESOLVABLE -> {
                val resolvable = workingSymbolTable.getNumberResolvable(lexeme.symbolId)
                    ?: return Result.failure(InternalFailure())
                return Result.success(ResolvableResult.of(resolvable))
            }
            Token.STRING_RESOLVABLE -> {
                val resolvable = workingSymbolTable.getStringResolvable(lexeme.symbolId)
                    ?: return Result.failure(InternalFailure())
                return Result.success(ResolvableResult.of(resolvable))
            }
            else -> return Result.failure(Throwable("Not a resolvable token"))
        }
    }
}