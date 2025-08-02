package com.loopers.domain.brand

data class BrandGetDto(
    val id: Long,
    val name: String,
) {
    companion object {
        fun of(source: BrandEntity): BrandGetDto {
            return BrandGetDto(
                id = source.id,
                name = source.name
            )
        }
    }
}
