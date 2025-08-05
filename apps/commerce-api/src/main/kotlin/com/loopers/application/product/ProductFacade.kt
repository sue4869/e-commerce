package com.loopers.application.product

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductLikeService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.product.ProductV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class ProductFacade(
    private val productService: ProductService,
    private val productLikeService: ProductLikeService,
    private val userService: UserService,
) {

    fun get(productId: Long): ProductV1Models.Response.GetInfo {
        val source = productService.getWithBrand(productId)
        return ProductV1Models.Response.GetInfo.of(source)
    }

    fun getList(command: ProductCommand.QueryCriteria): Page<ProductV1Models.Response.GetList> {
        val sourcePage = productService.getList(command)
        return sourcePage.map { ProductV1Models.Response.GetList.of(it) }
    }

    @Transactional
    fun like(command: ProductCommand.Like) {
        userService.findByUserId(command.userId) ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID,"존재하지 않는 사용자 ID 입니다. 사용자 ID: ${command.userId}")
        if (productLikeService.create(command)) {
            productService.like(command.productId)
        }
    }

    @Transactional
    fun deleteLike(command: ProductCommand.Like) {
        userService.findByUserId(command.userId) ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID,"존재하지 않는 사용자 ID 입니다. 사용자 ID: ${command.userId}")
        if (productLikeService.delete(command)) {
            productService.decreaseLike(command.productId)
        }
    }
}
