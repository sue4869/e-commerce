package com.loopers.support.querydsl

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.core.types.dsl.StringExpression

fun <T> SimpleExpression<T>.eqNotNull(right: T?): BooleanExpression? {
    if (right == null) return null
    return eq(right)
}

fun <T, S : T> SimpleExpression<T>.inNotNull(right: Collection<S>?): BooleanExpression? {
    if (right == null) return null
    return `in`(right)
}

fun <T, S : T> SimpleExpression<T>.inNotEmpty(right: Collection<S>?): BooleanExpression? {
    if (right.isNullOrEmpty()) return null
    return `in`(right)
}

fun <T, S : T> SimpleExpression<T>.notInNotNull(right: Collection<S>?): BooleanExpression? {
    if (right == null) return null
    return notIn(right)
}

fun <T : Comparable<*>> ComparableExpression<T>.loeNotNull(right: T?): BooleanExpression? {
    if (right == null) return null
    return loe(right)
}

fun <T : Comparable<*>> ComparableExpression<T>.gtNotNull(right: T?): BooleanExpression? {
    if (right == null) return null
    return gt(right)
}

fun StringExpression.containsNotNull(right: String?): BooleanExpression? {
    if (right == null) return null
    return contains(right)
}

fun <T> NumberExpression<T>.loeNotNull(right: T?): BooleanExpression? where T : Number, T : Comparable<*> {
    if (right == null) return null
    return loe(right)
}

fun StringExpression.startsWithNotNull(right: String?): BooleanExpression? {
    if (right == null) return null
    return startsWith(right)
}
