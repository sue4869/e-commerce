package com.loopers.fixture.product

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.product.ProductEntity
import com.loopers.fixture.point.PointFixture
import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

sealed class ProductFixture {

    abstract val name: String
    abstract val price: BigDecimal
    abstract val stock: Int
    abstract val likeCount: Int

    fun create(brandId: Long): ProductEntity {
        return ProductEntity(
            name = name,
            brandId = brandId,
            price = price,
            stock = stock,
            likeCount = likeCount
        )
    }

    fun createByPrice(
        brandId: Long,
        price: BigDecimal,
    ): ProductEntity {
        return ProductEntity(
            name = name,
            brandId = brandId,
            price = price,
            stock = stock,
            likeCount = likeCount
        )
    }

    fun createByLikeCount(
        brandId: Long,
        likeCount: Int,
    ): ProductEntity {
        return ProductEntity(
            name = name,
            brandId = brandId,
            price = price,
            stock = stock,
            likeCount = likeCount
        )
    }

    object Normal : ProductFixture() {
        override val name = "기본 상품"
        override val price = BigDecimal.valueOf(1000.00)
        override val stock = 100
        override val likeCount = 0
    }
}

