package com.loopers.domain.product

import org.springframework.data.domain.Page

interface ProductRepository {

    fun save(product: ProductEntity): ProductEntity

    fun saveAll(product: List<ProductEntity>): List<ProductEntity>

    fun findById(productId: Long): ProductEntity

    fun findListByCriteria(command: ProductCommand.QueryCriteria): Page<ProductListGetDto>

    fun findByIdIn(ids: Collection<Long>): List<ProductEntity>

    fun getWithBrandById(id: Long): ProductEntity

    fun incrementLikeCount(productId: Long): Int

    fun decrementLikeCount(productId: Long)
}
