package com.loopers.domain.brand

import com.loopers.interfaces.api.brand.BrandV1Models


interface BrandRepository {

    fun save(brand: BrandEntity): BrandEntity

    fun findById(id: Long): BrandEntity?

}
