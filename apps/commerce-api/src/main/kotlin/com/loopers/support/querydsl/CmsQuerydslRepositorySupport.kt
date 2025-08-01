package com.loopers.support.querydsl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

abstract class CmsQuerydslRepositorySupport(domainClass: Class<*>?) :
    DefaultQuerydslRepositorySupport(domainClass)
{
    @PersistenceContext(unitName = "entityManagerFactory")
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }
}
