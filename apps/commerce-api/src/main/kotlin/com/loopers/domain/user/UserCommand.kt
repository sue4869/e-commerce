package com.loopers.domain.user

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

const val USER_ID_REGEX = "^[a-zA-Z0-9]{1,10}$"
const val email_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
const val birth_REGEX = "^\\d{4}-\\d{2}-\\d{2}$"

class UserCommand {
    data class Create(
        val userId: String,
        val email: String,
        val gender: Gender,
        val birth: String,
    ) {
        init {
            require(userId.isNotBlank()) { "User id must not be blank" }
            require(email.isNotBlank()) { "User email must not be blank" }
            require(birth.isNotBlank()) { "Birth must not be blank"}

            require (userId.matches(USER_ID_REGEX.toRegex())) {
                throw CoreException(ErrorType.INVALID_USER_ID_FORMAT)
            }

            require (birth.matches(birth_REGEX.toRegex())) {
                throw CoreException(ErrorType.INVALID_USER_BIRTH_DAY_FORMAT)
            }

            require (email.matches(email_REGEX.toRegex())) {
                throw CoreException(ErrorType.INVALID_USER_EMAIL_FORMAT)
            }
        }
    }

    data class UserInfo(
        val id: Long,
        val userId: String,
        val email: String,
        val gender: Gender,
        val birth: String,
    ) {
        companion object {
            fun of(user: UserEntity): UserInfo {
                return UserInfo(
                    id = user.id,
                    userId = user.userId,
                    email = user.email,
                    gender = user.gender,
                    birth = user.birthDate
                )
            }
        }
    }

}
