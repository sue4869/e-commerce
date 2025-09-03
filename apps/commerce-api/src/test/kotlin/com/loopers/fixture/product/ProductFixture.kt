package com.loopers.fixture.product

import com.loopers.domain.product.ProductEntity

sealed class ProductFixture {

    abstract val name: String
    abstract val price: Long
    abstract val stock: Int

    fun create(brandId: Long): ProductEntity {
        return ProductEntity(
            name = name,
            brandId = brandId,
            price = price,
            stock = stock,
        )
    }

    fun createByPrice(
        brandId: Long,
        price: Long,
    ): ProductEntity {
        return ProductEntity(
            name = name,
            brandId = brandId,
            price = price,
            stock = stock,
        )
    }

    fun createByStock(
        brandId: Long,
        stock: Int,
    ): ProductEntity {
        return ProductEntity(
            name = name,
            brandId = brandId,
            price = price,
            stock = stock,
        )
    }

    object Normal : ProductFixture() {
        override val name = "기본 상품"
        override val price = 1000L
        override val stock = 100
    }
}
