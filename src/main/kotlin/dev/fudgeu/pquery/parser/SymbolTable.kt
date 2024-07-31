package dev.fudgeu.pquery.parser

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.NumberResolvable
import dev.fudgeu.pquery.resolvables.basic.StringResolvable
import dev.fudgeu.pquery.resolvables.comparison.ComparisonOperatorConstructor
import dev.fudgeu.pquery.resolvables.logical.LogicalOperatorConstructor
import dev.fudgeu.pquery.resolvables.math.MathOperatorConstructor

class SymbolTable(
    copyFrom: SymbolTable? = null,
) {

    private val booleanSymbols = mutableListOf<BooleanResolvable>()
    private val numberSymbols = mutableListOf<NumberResolvable>()
    private val stringSymbols = mutableListOf<StringResolvable>()
    private val logicalOperatorSymbols = mutableListOf<LogicalOperatorConstructor>()
    private val comparisonOperatorSymbols = mutableListOf<ComparisonOperatorConstructor>()
    private val mathConstructors = mutableListOf<MathOperatorConstructor>()

    init {
        if (copyFrom != null) {
            booleanSymbols.addAll(copyFrom.booleanSymbols)
            numberSymbols.addAll(copyFrom.numberSymbols)
            stringSymbols.addAll(copyFrom.stringSymbols)
            logicalOperatorSymbols.addAll(copyFrom.logicalOperatorSymbols)
            comparisonOperatorSymbols.addAll(copyFrom.comparisonOperatorSymbols)
            mathConstructors.addAll(copyFrom.mathConstructors)
        }
    }

    fun addBooleanResolvable(value: BooleanResolvable): Int {
        booleanSymbols.add(value)
        return booleanSymbols.size - 1
    }

    fun getBooleanResolvable(id: Int): BooleanResolvable? {
        return booleanSymbols[id]
    }

    fun addNumberResolvable(value: NumberResolvable): Int {
        numberSymbols.add(value)
        return numberSymbols.size - 1
    }

    fun getNumberResolvable(id: Int): NumberResolvable? {
        return numberSymbols[id]
    }

    fun addStringResolvable(value: StringResolvable): Int {
        stringSymbols.add(value)
        return stringSymbols.size - 1
    }

    fun getStringResolvable(id: Int): StringResolvable? {
        return stringSymbols[id]
    }

    fun addLogicalOperator(value: LogicalOperatorConstructor): Int {
        logicalOperatorSymbols.add(value)
        return logicalOperatorSymbols.size - 1
    }

    fun getLogicalOperator(id: Int): LogicalOperatorConstructor? {
        return logicalOperatorSymbols[id]
    }

    fun addComparisonOperator(value: ComparisonOperatorConstructor): Int {
        comparisonOperatorSymbols.add(value)
        return comparisonOperatorSymbols.size - 1
    }

    fun getComparisonOperator(id: Int): ComparisonOperatorConstructor? {
        return comparisonOperatorSymbols[id]
    }

    fun addMathConstructor(value: MathOperatorConstructor): Int {
        mathConstructors.add(value)
        return mathConstructors.size - 1
    }

    fun getMathConstructor(id: Int): MathOperatorConstructor? {
        return mathConstructors[id]
    }

    fun getSetOfPrecedences(): List<Int> {
        val result = mutableSetOf<Int>()
        for (mathConstructor in mathConstructors) {
            result.add(mathConstructor.precedence)
        }
        return result.sorted()
    }
}