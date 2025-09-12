package com.loopers.interfaces.api.product

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Tag(name = "Product V1 Api", description = "상품 조회")
interface ProductV1ApiSpec {

    fun get(@PathVariable productId: Long): ApiResponse<ProductV1Models.Response.GetInfo>

    fun getList(
        @ParameterObject request: ProductV1Models.Request.GetList,
        @ParameterObject pageable: Pageable,
    ): ApiResponse<Page<ProductV1Models.Response.GetList>>

    fun getRankingDaily(
        @RequestParam date: LocalDate,
        @ParameterObject pageable: Pageable
    ): ApiResponse<Page<ProductV1Models.Response.GetRank>>

    fun like(request: HttpServletRequest, productId: Long): ApiResponse<Unit>

    fun dislike(request: HttpServletRequest, productId: Long): ApiResponse<Unit>
}
