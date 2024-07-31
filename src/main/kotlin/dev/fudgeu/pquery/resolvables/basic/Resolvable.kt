package dev.fudgeu.pquery.resolvables.basic

interface Resolvable<T> {
    fun resolve(): T
}