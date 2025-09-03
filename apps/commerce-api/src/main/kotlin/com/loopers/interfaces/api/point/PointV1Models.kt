package com.loopers.interfaces.api.point

import com.loopers.domain.point.PointCommand
import jakarta.validation.constraints.NotNull

class PointV1Models {

    class Request {

        data class Charge(
            @field:NotNull
            val amount: Long,
        ) {
            fun toCommand(userId: String) = PointCommand.ChargeInput(
                    userId = userId,
                    amount = amount,
                )
            }
        }

    class Response {

        data class Get(
            val userId: String,
            val amount: Long,
        ) {
            companion object {
                fun of(command: PointCommand.PointInfo): Get {
                    return Get(
                        userId = command.userId,
                        amount = command.amount,
                    )
                }
            }
        }

        data class Charge(
            val amount: Long,
        ) {
            companion object {
                fun of(command: PointCommand.PointInfo): Charge {
                    return Charge(
                        amount = command.amount,
                    )
                }
            }
        }
    }
}
