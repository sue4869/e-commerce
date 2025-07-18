package com.loopers.interfaces.api.user

import com.loopers.domain.user.Gender
import com.loopers.domain.user.UserCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class UserV1Dto {

    class Request {

        data class SignUp(
            @NotBlank
            val userId: String,
            @NotBlank
            val birthDate: String,
            @NotBlank
            val email: String,
            @NotNull
            val gender: Gender
        ) {

            fun toCommand(): UserCommand.Create {
                return UserCommand.Create(
                    userId = userId,
                    birth = birthDate,
                    email = email,
                    gender = gender
                )
            }

        }
    }

    class Response {

        data class UserResponse(
            val userId: String,
            val birthDate: String,
            val email: String,
            val gender: Gender
        ) {
            companion object {
                fun of(command: UserCommand.UserInfo): UserResponse {
                    return UserResponse(
                        userId = command.userId,
                        birthDate = command.birth,
                        email = command.email,
                        gender = command.gender
                    )
                }
            }
        }

    }

}
