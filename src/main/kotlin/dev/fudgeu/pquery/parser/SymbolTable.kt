package dev.fudgeu.pquery.parser

import dev.fudgeu.pquery.resolvables.basic.BooleanResolvable
import dev.fudgeu.pquery.resolvables.basic.NumberResolvable
import dev.fudgeu.pquery.resolvables.basic.Resolvable
import dev.fudgeu.pquery.resolvables.basic.StringResolvable
import dev.fudgeu.pquery.resolvables.comparison.ComparisonOperatorConstructor
import dev.fudgeu.pquery.resolvables.logical.LogicalOperatorConstructor
import dev.fudgeu.pquery.resolvables.math.MathOperatorConstructor

class SymbolTable(
    copyFrom: SymbolTable? = null,
) {

    private val booleanSymbols = mutableListOf<Resolvable<Boolean>>()
    private val numberSymbols = mutableListOf<Resolvable<Double>>()
    private val stringSymbols = mutableListOf<Resolvable<String>>()
    private val numberListSymbols = mutableListOf<Resolvable<List<Double>>>()
    private val stringListSymbols = mutableListOf<Resolvable<List<String>>>()
    private val logicalOperatorSymbols = mutableListOf<LogicalOperatorConstructor>()
    private val comparisonOperatorSymbols = mutableListOf<ComparisonOperatorConstructor>()
    private val mathConstructors = mutableListOf<MathOperatorConstructor>()

    init {
        if (copyFrom != null) {
            booleanSymbols.addAll(copyFrom.booleanSymbols)
            numberSymbols.addAll(copyFrom.numberSymbols)
            stringSymbols.addAll(copyFrom.stringSymbols)
            numberListSymbols.addAll(copyFrom.numberListSymbols)
            stringListSymbols.addAll(copyFrom.stringListSymbols)
            logicalOperatorSymbols.addAll(copyFrom.logicalOperatorSymbols)
            comparisonOperatorSymbols.addAll(copyFrom.comparisonOperatorSymbols)
            mathConstructors.addAll(copyFrom.mathConstructors)
        }
    }

    fun addBooleanResolvable(value: Resolvable<Boolean>): Int {
        booleanSymbols.add(value)
        return booleanSymbols.size - 1
    }

    fun getBooleanResolvable(id: Int): Resolvable<Boolean>? {
        return booleanSymbols[id]
    }

    fun addNumberResolvable(value: Resolvable<Double>): Int {
        numberSymbols.add(value)
        return numberSymbols.size - 1
    }

    fun getNumberResolvable(id: Int): Resolvable<Double>? {
        return numberSymbols[id]
    }

    fun addStringResolvable(value: Resolvable<String>): Int {
        stringSymbols.add(value)
        return stringSymbols.size - 1
    }

    fun getStringResolvable(id: Int): Resolvable<String>? {
        return stringSymbols[id]
    }

    fun addNumberListResolvable(value: Resolvable<List<Double>>): Int {
        numberListSymbols.add(value)
        return numberListSymbols.size - 1
    }

    fun getNumberListResolvable(id: Int): Resolvable<List<Double>>? {
        return numberListSymbols[id]
    }

    fun addStringListResolvable(value: Resolvable<List<String>>): Int {
        stringListSymbols.add(value)
        return stringListSymbols.size - 1
    }

    fun getStringListResolvable(id: Int): Resolvable<List<String>>? {
        return stringListSymbols[id]
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