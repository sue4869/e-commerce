package com.loopers.domain.ranking

import com.loopers.domain.type.TargetType
import com.loopers.domain.type.WeightType

interface RankingWeightRepository {

    fun findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(
        targetType: TargetType,
        weightType: WeightType
    ): RankingWeightEntity?
}
