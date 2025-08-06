package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update product_like set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "product_like")
class ProductLikeEntity(
    userId: String,
    productId: Long
) : BaseEntity() {

    @Column(name = "user_id")
    val userId = userId

    @Column(name = "product_id")
    val productId = productId

    companion object {

        fun of(command: ProductCommand.Like): ProductLikeEntity {
            return ProductLikeEntity(
                userId = command.userId,
                productId = command.productId,
            )
        }
    }
}
