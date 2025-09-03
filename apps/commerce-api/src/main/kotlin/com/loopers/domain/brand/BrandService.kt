package com.loopers.domain.brand

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class BrandService(
    private val brandRepository: BrandRepository,
) {

    fun getById(brandId: Long): BrandGetDto {
        val brand = brandRepository.findById(brandId) ?: throw CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다.")
        return BrandGetDto.of(brand)
    }
}
