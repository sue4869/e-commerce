package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

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
}
