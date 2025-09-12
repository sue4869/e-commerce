package com.loopers.interfaces.api.product

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductCountDto
import com.loopers.domain.product.ProductWithBrandDto
import com.loopers.domain.product.ProductListGetDto
import com.loopers.domain.product.ProductRankDto
import org.springframework.data.domain.Pageable

class ProductV1Models {

    class Request {

        data class GetList(
            val brandIds: List<Long>,
            val productId: Long?,
        ) {
            fun toCommand(pageable: Pageable) = ProductCommand.QueryCriteria(
                brandIds = brandIds,
                productId = productId,
                pageable = pageable,
            )
        }
    }

    class Response {

        data class GetInfo(
            val productId: Long,
            val productName: String,
            val brandId: Long,
            val brandName: String,
            val likeCount: Int,
            val rank: Long?,
            val stock: Int,
            val price: Long,
        ) {
            companion object {
                fun of(dto: ProductWithBrandDto, productCount: ProductCountDto): GetInfo {
                    return GetInfo(
                        productId = dto.productId,
                        productName = dto.productName,
                        brandId = dto.brandId,
                        brandName = dto.brandName,
                        likeCount = productCount.likeCount,
                        rank = dto.rank,
                        stock = dto.stock,
                        price = dto.price,
                    )
                }
            }
        }

        data class GetList(
            val productId: Long,
            val productName: String,
            val brandId: Long,
            val brandName: String,
            val likeCount: Int,
            val price: Long,
        ) {
            companion object {
                fun of(dto: ProductListGetDto): GetList {
                    return GetList(
                        dto.productId,
                        dto.productName,
                        dto.brandId,
                        dto.brandName,
                        dto.likeCount,
                        dto.price,
                    )
                }
            }
        }

        data class GetRank(
            val rank: Int,
            val productId: Long,
            val productName: String,
            val score: Double,
        ) {
            companion object {
                fun of(dto: ProductRankDto): GetRank {
                    return GetRank(
                        rank = dto.rank,
                        productId = dto.productId,
                        productName = dto.productName,
                        score = dto.score,
                    )
                }
            }
        }
    }
}
