package com.loopers.domain.product

data class ProductHistoryDto(
    val productId: Long,
    val productHistoryId: Long,
    val productName: String,
    val brandId: Long,
    val stock: Int,
) {
    companion object {
        fun of(source: ProductHistoryEntity) = ProductHistoryDto(
            productId = source.productId,
            productHistoryId = source.id,
            productName = source.name,
            brandId = source.brandId,
            stock = source.stock,
        )
    }
}

data class ProductWithBrandDto(
    val productId: Long,
    val productName: String,
    val brandId: Long,
    val brandName: String,
    val stock: Int,
    val price: Long,
) {
    companion object {
        fun of(source: ProductEntity): ProductWithBrandDto {
            return ProductWithBrandDto(
                productId = source.id,
                productName = source.name,
                brandId = source.brandId,
                brandName = source.brand!!.name,
                stock = source.stock,
                price = source.price,
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
    val price: Long,
)

data class ProductToUserLikeDto(
    val productId: Long,
    val userId: String,
)

data class ProductCountDto(
    val productId: Long,
    val likeCount: Int,
)
