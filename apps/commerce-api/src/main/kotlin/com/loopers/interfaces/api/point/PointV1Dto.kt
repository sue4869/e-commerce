package com.loopers.interfaces.api.point

import com.loopers.domain.point.PointCommand
import java.math.BigDecimal

class PointV1Dto {

    class Response {

        data class PointResponse(
            val userId: String,
            val amount: Long,
        ) {
            companion object {
                fun of(command: PointCommand.PointInfo): PointResponse {
                    return PointResponse(
                        userId = command.userId,
                        amount = command.amount
                    )
                }
            }
        }
    }
}
