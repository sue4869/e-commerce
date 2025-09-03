package com.loopers.infrastructure.product

import com.loopers.domain.brand.QBrandEntity
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductListGetDto
import com.loopers.domain.product.QProductCountEntity
import com.loopers.domain.product.QProductEntity
import com.loopers.support.querydsl.CmsQuerydslRepositorySupport
import com.loopers.support.querydsl.eqNotNull
import com.loopers.support.querydsl.inNotEmpty
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.support.PageableExecutionUtils

interface ProductRepositoryCustom {
    fun findListByCriteria(command: ProductCommand.QueryCriteria): Page<ProductListGetDto>
}

class ProductRepositoryCustomImpl : CmsQuerydslRepositorySupport(ProductEntity::class.java), ProductRepositoryCustom {

    companion object {
        private val product = QProductEntity.productEntity
        private val brand = QBrandEntity.brandEntity
        private val productCount = QProductCountEntity.productCountEntity
    }

    override fun findListByCriteria(command: ProductCommand.QueryCriteria): Page<ProductListGetDto> {
        val where = BooleanBuilder().apply {
            and(product.brandId.inNotEmpty(command.brandIds))
            and(product.id.eqNotNull(command.productId))
        }

        val from = from(product)
            .innerJoin(brand).on(product.brandId.eq(brand.id))
            .innerJoin(productCount).on(productCount.productId.eq(product.id))
            .where(where)

        val result = from
            .orderBy(order(command.pageable.sort))
            .offset(command.pageable.offset)
            .select(
                Projections.constructor(
                    ProductListGetDto::class.java,
                    product.id,
                    product.name,
                    product.brandId,
                    brand.name,
                    productCount.likeCount,
                    product.price,
                ),
            ).fetch()

        return PageableExecutionUtils.getPage(result, command.pageable, from::fetchCount)
    }

    private fun order(sort: Sort): OrderSpecifier<*> {
        val first = sort.firstOrNull()
        return if (first != null) {
            OrderSpecifier(
                if (first.isAscending) Order.ASC else Order.DESC,
                if (first.property == "latest") {
                    product.id
                } else if (first.property == "price") {
                    product.price
                } else if (first.property == "likes") {
                    productCount.likeCount
                } else {
                    null
                },
            )
        } else {
            OrderSpecifier(Order.DESC, product.id)
        }
    }
}
