package com.loopers.infrastructure.ranking

import com.loopers.domain.ranking.RankingWeightEntity
import com.loopers.domain.type.TargetType
import com.loopers.domain.type.WeightType
import org.springframework.data.jpa.repository.JpaRepository

interface RankingWeightJpaRepository: JpaRepository<RankingWeightEntity, Long>  {

    fun findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(
        targetType: TargetType,
        weightType: WeightType
    ): RankingWeightEntity?
}
