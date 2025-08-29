package com.loopers.application.product

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.ProductDislikedEvent
import com.loopers.domain.event.dto.ProductLikedEvent
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductCountService
import com.loopers.domain.product.ProductToUserLikeService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.product.ProductV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class ProductFacade(
    private val productService: ProductService,
    private val productToUserLikeService: ProductToUserLikeService,
    private val productCountService: ProductCountService,
    private val userService: UserService,
    private val eventPublisher: EventPublisher,
) {

    private val log = KotlinLogging.logger {}

    fun get(productId: Long): ProductV1Models.Response.GetInfo {
        val source = productService.getWithBrand(productId)
        val countDto = productCountService.getByProductId(productId)
        return ProductV1Models.Response.GetInfo.of(source, countDto)
    }

    fun getList(command: ProductCommand.QueryCriteria): Page<ProductV1Models.Response.GetList> {
        val sourcePage = productService.getList(command)
        return sourcePage.map { ProductV1Models.Response.GetList.of(it) }
    }

    @Transactional
    fun like(command: ProductCommand.Like) {
        userService.findByUserId(command.userId) ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID,"존재하지 않는 사용자 ID 입니다. 사용자 ID: ${command.userId}")
        if (productToUserLikeService.create(command)) {
            eventPublisher.publish(ProductLikedEvent(command.productId))
            log.info( "publish ProductLikedEvent productId: ${command.productId}")
        }
    }

    @Transactional
    fun dislike(command: ProductCommand.Like) {
        userService.findByUserId(command.userId) ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID,"존재하지 않는 사용자 ID 입니다. 사용자 ID: ${command.userId}")
        if (productToUserLikeService.delete(command)) {
            eventPublisher.publish(ProductDislikedEvent(command.productId))
            log.info( "publish ProductDislikedEvent productId: ${command.productId}")
        }
    }
}
