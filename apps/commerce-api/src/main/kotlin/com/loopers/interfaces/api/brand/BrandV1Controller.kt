package com.loopers.interfaces.api.brand

import com.loopers.application.brand.BrandFacade
import com.loopers.interfaces.api.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/brand")
class BrandV1Controller(
    private val brandFacade: BrandFacade
) {
    @GetMapping("/{brandId}")
    fun get(@PathVariable brandId: Long): ApiResponse<BrandV1Models.Response.Info> {
        return ApiResponse.success(brandFacade.get(brandId))
    }
}
