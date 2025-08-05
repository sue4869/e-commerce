package com.loopers.domain.product

import java.math.BigDecimal

data class ProductHistoryDto(
    val productId: Long,
    val productHistoryId: Long,
    val productName: String,
    val brandId: Long,
    val stock: Int,
    val likeCount: Int
) {
    companion object {
        fun of(source: ProductHistoryEntity) = ProductHistoryDto(
            productId = source.productId,
            productHistoryId = source.id,
            productName = source.name,
            brandId = source.brandId,
            stock = source.stock,
            likeCount = source.likeCount
        )
    }
}

data class ProductWithBrandDto(
    val productId: Long,
    val productName: String,
    val brandId: Long,
    val brandName: String,
    val stock: Int,
    val likeCount: Int,
    val price: BigDecimal
) {
    companion object {
        fun of(source: ProductEntity): ProductWithBrandDto {
            return ProductWithBrandDto(
                productId = source.id,
                productName = source.name,
                brandId = source.brandId,
                brandName = source.brand!!.name,
                stock = source.stock,
                likeCount = source.likeCount,
                price = source.price
            )
        }
    }
}

data class ProductListGetDto(
    val productId: Long,
    val productName: String,
    val brandId: Long,
    val brandName: String,
    val likeCount: Int,
    val price: BigDecimal
)

data class ProductLikeDto(
    val productId: Long,
    val userId: String,
)
