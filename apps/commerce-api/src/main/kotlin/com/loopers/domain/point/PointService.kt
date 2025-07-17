package com.loopers.domain.point

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PointService(
    private val pointRepository: PointRepository
) {

    fun findByUserId(userId: String): PointCommand.PointInfo? =
        pointRepository.findByUserId(userId)?.let { PointCommand.PointInfo.of(it) }
}
