package com.loopers.application.brand

import com.loopers.domain.brand.BrandService
import com.loopers.interfaces.api.brand.BrandV1Models
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class BrandFacade(
    private val brandService: BrandService,
) {

    fun get(brandId: Long): BrandV1Models.Response.Info {
        return BrandV1Models.Response.Info.of(brandService.getById(brandId))
    }
}
