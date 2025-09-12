package com.loopers.domain.ranking

import com.loopers.domain.BaseEntity
import com.loopers.domain.type.TargetType
import com.loopers.domain.type.WeightType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "ranking_weight")
class RankingWeightEntity(
    targetType: TargetType,
    weightType: WeightType,
    weight: Double,
) : BaseEntity() {

    @Enumerated(EnumType.STRING)
    val targetType: TargetType = targetType

    @Enumerated(EnumType.STRING)
    val weightType: WeightType = weightType

    val weight: Double = weight

    companion object {
        fun of(
            targetType: TargetType,
            weightType: WeightType,
            weight: Double,
        ): RankingWeightEntity {
            return RankingWeightEntity(
                targetType = targetType,
                weightType = weightType,
                weight = weight,
            )
        }
    }
}
