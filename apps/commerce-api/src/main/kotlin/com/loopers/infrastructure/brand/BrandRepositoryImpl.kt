package com.loopers.infrastructure.brand

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.brand.BrandRepository
import org.springframework.stereotype.Component

@Component
class BrandRepositoryImpl(
    private val brandJpaRepository: BrandJpaRepository
): BrandRepository {

    override fun save(brand: BrandEntity): BrandEntity {
        return brandJpaRepository.save(brand)
    }

    override fun findById(id: Long): BrandEntity? {
        return brandJpaRepository.findById(id).orElse(null)
    }
}
