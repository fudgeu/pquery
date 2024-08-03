package dev.fudgeu.pquery.resolvables.basic

interface Resolvable<out T> {
    fun resolve(): T
}