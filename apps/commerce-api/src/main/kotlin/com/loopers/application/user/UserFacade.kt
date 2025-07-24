package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.user.UserV1Dto
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserFacade(
    private val userService: UserService,
) {

    @Transactional
    fun signUp(command: UserCommand.Create): UserV1Dto.Response.UserResponse {

        userService.findByUserId(command.userId)?.let {
            throw CoreException(ErrorType.CONFLICT, "이미 회원가입된 사용자입니다.")
        }

        return UserV1Dto.Response.UserResponse.of(userService.create(command))
    }

    @Transactional(readOnly = true)
    fun getMyInfo(userId: String): UserV1Dto.Response.UserResponse =
        userService.findByUserId(userId)
            ?.let(UserV1Dto.Response.UserResponse::of)
            ?: throw CoreException(ErrorType.NOT_EXIST_USER)
}
