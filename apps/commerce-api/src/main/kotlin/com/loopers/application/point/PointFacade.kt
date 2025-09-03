package com.loopers.application.point

import com.loopers.domain.point.PointCommand
import com.loopers.domain.point.PointService
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.point.PointV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PointFacade(
    private val pointService: PointService,
    private val userService: UserService,
) {

    @Transactional(readOnly = true)
    fun get(userId: String): PointV1Models.Response.Get {
        val pointInfo = pointService.findByUserId(userId)
            ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "존재하지 않는 사용자 ID 입니다. 사용자 ID: $userId")
        return pointInfo.let { PointV1Models.Response.Get.of(it) }
    }

    @Transactional
    fun charge(command: PointCommand.ChargeInput): PointV1Models.Response.Charge {
        userService.findByUserId(command.userId)
            ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "존재하지 않는 사용자 ID 입니다. 사용자 ID: ${command.userId}")
        return pointService.charge(command).let { PointV1Models.Response.Charge.of(it) }
    }
}
