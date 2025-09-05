package com.loopers.domain.metrics

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "product_metrics")
class ProductMetricsEntity(
    val productId: Long,
    val metricDate: LocalDate,
    var likeCount: Int = 0,
    var viewCount: Int = 0,
    var orderCount: Int = 0,
) : BaseEntity() {

    init {
        require(likeCount >= 0) { "상품 좋아요 수는 양수여야 합니다." }
        require(viewCount >= 0) { "상품 조회 수는 양수여야 합니다." }
        require(orderCount >= 0) { "상품 판매 수는 양수여야 합니다." }
    }

    fun increaseLikeCount() {
        likeCount++
    }

    fun increaseViewCount() {
        viewCount++
    }

    fun increaseOrderCount() {
        orderCount++
    }

    fun decreaseLikeCount() {
        if (likeCount > 0) return
        likeCount--
    }

    companion object {
        fun of(productId: Long): ProductMetricsEntity {
            return ProductMetricsEntity(
                productId = productId,
                metricDate = LocalDate.now()
            )
        }
    }
}
