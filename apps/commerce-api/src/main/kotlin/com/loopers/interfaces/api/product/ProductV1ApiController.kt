package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductFacade
import com.loopers.domain.product.ProductCommand
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductV1ApiController(
    private val productFacade: ProductFacade
): ProductV1ApiSpec {

    @GetMapping("/{productId}")
    override fun get(@PathVariable productId: Long) : ApiResponse<ProductV1Models.Response.GetInfo> {
        return ApiResponse.success(productFacade.get(productId))
    }

    @GetMapping
    override fun getList(@ParameterObject request: ProductV1Models.Request.GetList, @ParameterObject pageable: Pageable): ApiResponse<Page<ProductV1Models.Response.GetList>> {
        return ApiResponse.success(productFacade.getList(request.toCommand(pageable)))
    }

    @GetMapping("/{productId}/likes")
    override fun like(
        request: HttpServletRequest,
        @PathVariable productId: Long,
    ): ApiResponse<Unit> {
        val userId = request.getHeader("X-USER-ID") ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "X-USER-ID is missing")
        return ApiResponse.success(productFacade.like(ProductCommand.Like(productId, userId)))
    }

    @DeleteMapping("/{productId}/likes")
    override fun dislike(
        request: HttpServletRequest,
        @PathVariable productId: Long,
    ): ApiResponse<Unit> {
        val userId = request.getHeader("X-USER-ID") ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "X-USER-ID is missing")
        return ApiResponse.success(productFacade.dislike(ProductCommand.Like(productId, userId)))
    }

}
