package com.loopers.infrastructure.point

import com.loopers.domain.point.PointEntity
import com.loopers.domain.point.PointRepository
import org.springframework.stereotype.Component

@Component
class PointRepositoryImpl(
    private val pointJpaRepository: PointJpaRepository
): PointRepository {

    override fun findByUserId(userId: String): PointEntity? {
        return pointJpaRepository.findByUserId(userId)
    }

    override fun save(point: PointEntity) {
        pointJpaRepository.save(point)
    }

}
