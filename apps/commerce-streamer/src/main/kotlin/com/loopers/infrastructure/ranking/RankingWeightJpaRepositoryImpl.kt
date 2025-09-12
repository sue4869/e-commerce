package com.loopers.infrastructure.ranking

import com.loopers.domain.ranking.RankingWeightEntity
import com.loopers.domain.ranking.RankingWeightRepository
import com.loopers.domain.type.TargetType
import com.loopers.domain.type.WeightType
import org.springframework.stereotype.Component

@Component
class RankingWeightJpaRepositoryImpl(
    private val rankingWeightJpaRepository: RankingWeightJpaRepository
): RankingWeightRepository {

    override fun findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(
        targetType: TargetType,
        weightType: WeightType,
    ): RankingWeightEntity? {
       return rankingWeightJpaRepository.findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(targetType, weightType)
    }
}
