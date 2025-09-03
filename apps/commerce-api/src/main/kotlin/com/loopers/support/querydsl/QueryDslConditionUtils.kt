package com.loopers.support.querydsl

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.core.types.dsl.StringExpression

fun <T> eq(expression: SimpleExpression<T>, value: T?): BooleanExpression? =
    value?.let { expression.eq(it) }

fun <T> ne(expression: SimpleExpression<T>, value: T?): BooleanExpression? =
    value?.let { expression.ne(it) }

fun like(expression: StringExpression, value: String?): BooleanExpression? =
    value?.let { expression.startsWith(it) }

fun contains(expression: StringExpression, value: String?): BooleanExpression? =
    value?.let { expression.contains(it) }

fun <T : Comparable<T>> loe(expression: ComparableExpression<T>, value: T?): BooleanExpression? =
    value?.let { expression.loe(it) }

fun <T : Comparable<T>> lt(expression: ComparableExpression<T>, value: T?): BooleanExpression? =
    value?.let { expression.lt(it) }

fun <T : Comparable<T>> goe(expression: ComparableExpression<T>, value: T?): BooleanExpression? =
    value?.let { expression.goe(it) }

fun <T : Comparable<T>> gt(expression: ComparableExpression<T>, value: T?): BooleanExpression? =
    value?.let { expression.gt(it) }

fun <T> inOrFalse(expression: SimpleExpression<T>, values: Collection<T?>?): BooleanExpression =
    if (!values.isNullOrEmpty()) {
        expression.`in`(values)
    } else {
        Expressions.FALSE
    }
