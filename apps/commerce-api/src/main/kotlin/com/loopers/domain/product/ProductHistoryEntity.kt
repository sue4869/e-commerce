package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "product_history")
class ProductHistoryEntity(
    productId: Long,
    name: String,
    brandId: Long,
    stock: Int = 0,
    likeCount: Int = 0,
): BaseEntity() {

    @Column(name = "product_id")
    val productId: Long = productId

    @Column(name = "name")
    val name: String = name

    @Column(name = "brand_id")
    val brandId: Long = brandId

    @Column(name = "stock")
    var stock: Int = stock

    @Column(name = "like_count")
    var likeCount: Int = likeCount

    companion object {
        fun of(product: ProductEntity): ProductHistoryEntity {
            return ProductHistoryEntity(
                product.id,
                product.name,
                product.brandId,
                product.likeCount)
        }
    }
}
