package com.natpryce.pottery

import java.time.LocalDate


data class Span<T : Comparable<T>>(val start: T, val endExclusive: T)

operator fun <T : Comparable<T>> Span<T>.contains(point: T) = point >= start && point < endExclusive

fun <T : Comparable<T>, U : Comparable<U>> Span<T>.map(f: (T) -> U) = Span(f(start), f(endExclusive))

fun <T : Comparable<T>> Span<T>.step(next: (T) -> T) = object : Iterable<T> {
    override fun iterator() =
        generateSequence(start, { current -> next(current).takeIf { it < endExclusive } }).iterator()
}

fun Span<LocalDate>.days() = step { date -> date.plusDays(1) }
