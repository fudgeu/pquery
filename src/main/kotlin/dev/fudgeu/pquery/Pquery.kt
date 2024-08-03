package dev.fudgeu.pquery

import dev.fudgeu.pquery.parser.Parser
import dev.fudgeu.pquery.parser.Tokenizer
import dev.fudgeu.pquery.plugin.Plugin
import dev.fudgeu.pquery.plugin.Plugins
import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.Resolvable
import dev.fudgeu.pquery.resolvables.comparison.EqualsResolver
import dev.fudgeu.pquery.resolvables.comparison.GreaterThanResolver
import dev.fudgeu.pquery.resolvables.comparison.InListResolver
import dev.fudgeu.pquery.resolvables.comparison.NotEqualsResolver
import dev.fudgeu.pquery.resolvables.logical.LogicalAndResolver
import dev.fudgeu.pquery.resolvables.logical.LogicalOrResolver
import dev.fudgeu.pquery.resolvables.math.*

class Pquery(plugins: List<Plugin> = listOf()) {
    private val registeredPlugins = Plugins()
    private val tokenizer: Tokenizer

    init {
        val basePlugin = Plugin(
            name = "base",
            comparisonOperators = mapOf(
                "==" to EqualsResolver.Constructor(),
                "!=" to NotEqualsResolver.Constructor(),
                ">" to GreaterThanResolver.Constructor(),
                "in" to InListResolver.Constructor(),
            ),
            logicalOperators = mapOf(
                "&&" to LogicalAndResolver.Constructor(),
                "||" to LogicalOrResolver.Constructor(),
            ),
            mathOperators = mapOf(
                "+" to AdditionResolver.Constructor(),
                "-" to SubtractionResolver.Constructor(),
                "*" to MultiplicationResolver.Constructor(),
                "/" to DivisionResolver.Constructor(),
                "^" to ExponentiationResolver.Constructor(),
                "%" to ModuloOperator.Constructor(),
            ),
            variables = mapOf(),
        )

        registeredPlugins.register(basePlugin)
        plugins.forEach { registeredPlugins.register(it) }

        tokenizer = Tokenizer(registeredPlugins)
    }

    fun compile(query: String): Result<Resolvable<Boolean>> {
        val tokenizedQuery = tokenizer.tokenize(query)
            .getOrElse { return Result.failure(it) }
        return Parser(tokenizedQuery, registeredPlugins).parse()
    }
}