package com.loopers.domain.user

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

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

            require (birth.matches(BIRTH_REGEX.toRegex())) {
                throw CoreException(ErrorType.INVALID_USER_BIRTH_DAY_FORMAT)
            }

            require (email.matches(EMAIL_REGEX.toRegex())) {
                throw CoreException(ErrorType.INVALID_USER_EMAIL_FORMAT)
            }
        }

        companion object {
            private const val USER_ID_REGEX = "^[a-zA-Z0-9]{1,10}$"
            private const val EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
            private const val BIRTH_REGEX = "^\\d{4}-\\d{2}-\\d{2}$"
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
