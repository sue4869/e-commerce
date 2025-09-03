package com.loopers.interfaces.api.brand

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "Brand V1 Api", description = "상품 조회")
interface BrandV1ApiSpec {

    fun get(@PathVariable brandId: Long): ApiResponse<BrandV1Models.Response.Info>
}
