package com.loopers.support.querydsl

import com.querydsl.core.types.EntityPath
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.AbstractJPAQuery
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPQLTemplates
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

abstract class DefaultQuerydslRepositorySupport(domainClass: Class<*>?): QuerydslRepositorySupport(domainClass!!) {
    private var querydsl: DefaultQuerydsl? = null

    override fun getQuerydsl(): Querydsl? {
        if (querydsl == null && entityManager != null) {
            querydsl = DefaultQuerydsl(entityManager!!, getBuilder<Any>())
        }
        return querydsl
    }

    override fun from(vararg paths: EntityPath<*>): JPQLQuery<Any> {
        return requiredQuerydsl!!.createQuery(*paths)
    }

    override fun <T> from(path: EntityPath<T>): JPQLQuery<T> {
        return requiredQuerydsl!!.createQuery(path).select(path)
    }

    private val requiredQuerydsl: Querydsl?
        get() {
            checkNotNull(getQuerydsl()) { "Querydsl is null" }

            return querydsl
        }


    class DefaultQuerydsl(
        private val entityManager: EntityManager,
        builder: PathBuilder<*>
    ) : Querydsl(entityManager, builder) {

        override fun <T> createQuery(): AbstractJPAQuery<T, JPAQuery<T>> {
            // https://github.com/querydsl/querydsl/issues/3428
            return JPAQuery(entityManager, JPQLTemplates.DEFAULT)
        }
    }
}
