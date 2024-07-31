package dev.fudgeu.pquery.parser

import dev.fudgeu.pquery.errors.InvalidToken
import dev.fudgeu.pquery.plugin.Plugins
import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.NumberResolvable
import dev.fudgeu.pquery.resolvables.basic.ResolvableType
import dev.fudgeu.pquery.resolvables.basic.StringResolvable
import java.util.regex.Pattern

class Tokenizer(
    private val plugins: Plugins,
) {
    private val numberPattern = Pattern.compile("^-?\\d+.?\\d*\$", Pattern.MULTILINE)

    fun tokenize(query: String): Result<TokenizedQuery> {
        val tokens = mutableListOf<Lexeme>()
        val symbolTable = SymbolTable()

        var workingQuery = query.trim()
        var at = 1
        while (workingQuery.isNotEmpty()) {
            // Check whitespace
            if (workingQuery[0].isWhitespace()) {
                workingQuery = workingQuery.drop(1)
                at++
                continue
            }

            // Check parenthesis
            if (workingQuery[0] == '(') {
                workingQuery = workingQuery.drop(1)
                tokens.add(Lexeme(Token.LEFT_PARENTHESIS, at = at))
                at++
                continue
            } else if (workingQuery[0] == ')') {
                workingQuery = workingQuery.drop(1)
                tokens.add(Lexeme(Token.RIGHT_PARENTHESIS, at = at))
                at++
                continue
            }

            // Check string literals
            if (workingQuery[0] == '\'' || workingQuery[0] == '"') {
                val quoteCharacter = workingQuery[0]
                workingQuery = workingQuery.substring(1)
                at++

                // Get full string
                val string = workingQuery.takeWhile { it != quoteCharacter }
                workingQuery = workingQuery.substring(string.length)

                // Register token
                val id = symbolTable.addStringResolvable(StringResolvable.of(string))
                tokens.add(Lexeme(Token.STRING_RESOLVABLE, id, at))
                at += string.length + 1
                continue
            }

            // For remainder of possible tokens, just grab all characters until a whitespace is hit
            val token = workingQuery.takeWhile { !it.isWhitespace() && it != ')' } // TODO need to detect more than just whitespace
            workingQuery = workingQuery.substring(token.length)

            // Check boolean literals
            if (token == "true") {
                val id = symbolTable.addBooleanResolvable(BooleanResolvable.of(true))
                tokens.add(Lexeme(Token.BOOLEAN_RESOLVABLE, id, at))
                at += token.length
                continue
            } else if (token == "false") {
                val id = symbolTable.addBooleanResolvable(BooleanResolvable.of(false))
                tokens.add(Lexeme(Token.BOOLEAN_RESOLVABLE, id, at))
                at += token.length
                continue
            }

            // Check number literals
            if (numberPattern.matcher(token).matches()) {
                // Register token
                val id = symbolTable.addNumberResolvable(NumberResolvable.of(token.toDouble()))
                tokens.add(Lexeme(Token.NUMBER_RESOLVABLE, id, at))
                at += token.length
                continue
            }

            // Check logical operators
            val logicalOperatorConstructor = plugins.getLogicalOperator(token)
            if (logicalOperatorConstructor != null) {
                val id = symbolTable.addLogicalOperator(logicalOperatorConstructor)
                tokens.add(Lexeme(Token.LOGICAL_OPERATOR, id, at))
                at += token.length
                continue
            }

            // Check comparison operators
            val comparisonOperatorConstructor = plugins.getComparisonOperator(token)
            if (comparisonOperatorConstructor != null) {
                val id = symbolTable.addComparisonOperator(comparisonOperatorConstructor)
                tokens.add(Lexeme(Token.COMPARISON_OPERATOR, id, at))
                at += token.length
                continue
            }

            // Check math operators
            val mathConstructor = plugins.getMathConstructor(token)
            if (mathConstructor != null) {
                val id = symbolTable.addMathConstructor(mathConstructor)
                tokens.add(Lexeme(Token.MATH_OPERATOR, id, at))
                at += token.length
                continue
            }

            // Check variables
            val possibleVariable = plugins.getVariable(token)
            if (possibleVariable != null) {
                when (possibleVariable.type) {
                    ResolvableType.BOOLEAN -> {
                        val id = symbolTable.addBooleanResolvable(possibleVariable.boolean!!)
                        tokens.add(Lexeme(Token.BOOLEAN_RESOLVABLE, id, at))
                    }
                    ResolvableType.NUMBER -> {
                        val id = symbolTable.addNumberResolvable(possibleVariable.number!!)
                        tokens.add(Lexeme(Token.NUMBER_RESOLVABLE, id, at))
                    }
                    ResolvableType.STRING -> {
                        val id = symbolTable.addStringResolvable(possibleVariable.string!!)
                        tokens.add(Lexeme(Token.STRING_RESOLVABLE, id, at))
                    }
                }
                at += token.length
                continue
            }

            // Token could not be identified, throw error
            return Result.failure(InvalidToken(token, at))
        }

        return Result.success(
            TokenizedQuery(tokens, symbolTable)
        )
    }
}