package com.loopers.application.point

import com.loopers.domain.point.PointService
import com.loopers.interfaces.api.point.PointV1Dto
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PointFacade(
    private val pointService: PointService
) {


    fun getPoint(userId: String): PointV1Dto.Response.PointResponse {
        val pointInfo = pointService.findByUserId(userId) ?: throw IllegalArgumentException("User not found by $userId")
        return pointInfo.let { PointV1Dto.Response.PointResponse.of(it) }
    }
}
