package com.loopers.domain.brand

interface BrandRepository {

    fun save(brand: BrandEntity): BrandEntity

    fun findById(id: Long): BrandEntity?
}
