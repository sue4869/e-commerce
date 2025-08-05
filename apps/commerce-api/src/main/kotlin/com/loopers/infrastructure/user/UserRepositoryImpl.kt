package com.loopers.infrastructure.user

import com.loopers.domain.user.UserEntity
import com.loopers.domain.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
): UserRepository {

    override fun save(user: UserEntity) : UserEntity {
        return userJpaRepository.save(user)
    }

    override fun findByUserId(userId: String): UserEntity? {
        return userJpaRepository.findByUserId(userId)
    }
}
