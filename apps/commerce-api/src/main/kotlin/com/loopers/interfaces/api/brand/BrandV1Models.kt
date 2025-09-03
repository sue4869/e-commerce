package com.loopers.interfaces.api.brand

import com.loopers.domain.brand.BrandGetDto

class BrandV1Models {

    class Response {

        data class Info(
            val id: Long,
            val name: String,
        ) {
            companion object {
                fun of(brand: BrandGetDto) = Info(
                    id = brand.id,
                    name = brand.name,
                )
            }
        }
    }
}
