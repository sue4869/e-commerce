package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.domain.brand.BrandEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "product")
class ProductEntity(
    name: String,
    brandId: Long,
    price: BigDecimal,
    stock: Int = 0,
    likeCount: Int = 0,
) : BaseEntity() {

    @Column(name = "name")
    val name: String = name

    @Column(name = "brand_id")
    var brandId: Long = brandId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    var brand: BrandEntity? = null

    @Column(name = "price")
    val price: BigDecimal = price

    @Column(name = "stock")
    var stock: Int = stock

    @Column(name = "like_count")
    var likeCount: Int = likeCount

    fun updateStock(qty: Int) {
        stock -= qty
    }
}

