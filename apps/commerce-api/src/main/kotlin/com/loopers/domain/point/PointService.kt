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

    @Transactional
    fun charge(command: PointCommand.ChargeInput): PointCommand.PointInfo {
        val point = pointRepository.findByUserId(command.userId) ?: PointEntity.of(command)
        point.charge(command.amount)
        pointRepository.save(point)

        return PointCommand.PointInfo.of(point)
    }
}
