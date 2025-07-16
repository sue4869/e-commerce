package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserFacade(
    private val userService: UserService,
) {

    @Transactional
    fun signUp(command: UserCommand.Create): UserCommand.UserInfo {

        userService.findByUserId(command.userId)?.let {
            throw CoreException(ErrorType.CONFLICT, "이미 회원가입된 사용자입니다.")
        }

        return userService.create(command)
    }

    @Transactional(readOnly = true)
    fun getMyInfo(userId: String): UserCommand.UserInfo =
        userService.findByUserId(userId) ?: throw CoreException(ErrorType.NOT_EXIST_USER)
}
