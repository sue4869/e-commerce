package com.loopers.interfaces.api.point

import com.loopers.domain.point.PointCommand
import jakarta.validation.constraints.NotNull

class PointV1Dto {

    class Request {

        data class Charge(
            @field:NotNull
            val amount: Long
        ) {
            fun toCommand(userId: String) = PointCommand.ChargeInput(
                    userId = userId,
                    amount = amount,
                )
            }
        }

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

        data class ChargeResponse(
            val amount: Long,
        ) {
            companion object {
                fun of(command: PointCommand.PointInfo): ChargeResponse {
                    return ChargeResponse(
                        amount = command.amount
                    )
                }
            }
        }
    }
}
