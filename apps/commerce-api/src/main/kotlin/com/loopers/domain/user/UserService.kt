package com.loopers.domain.user

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun findByUserId(userId : String): UserCommand.UserInfo? {
        return userRepository.findByUserId(userId)?.let { user -> UserCommand.UserInfo.of(user) }
    }

    @Transactional
    fun create(command: UserCommand.Create): UserCommand.UserInfo {
        val user = UserEntity.of(command)
        userRepository.save(user)
        return UserCommand.UserInfo.of(user)
    }
}
