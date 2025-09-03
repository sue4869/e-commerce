package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update product_count set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "product_count")
class ProductCountEntity(
    productId: Long,
    likeCount: Int = 0,
) : BaseEntity() {
    @Column(name = "product_id", nullable = false, unique = true)
    var productId: Long = productId

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = likeCount

    fun increaseLike(likeCount: Int) {
        this.likeCount += likeCount
    }

    fun decreaseLike(likeCount: Int) {
        if (this.likeCount == 0) return
        this.likeCount -= likeCount
    }
}
