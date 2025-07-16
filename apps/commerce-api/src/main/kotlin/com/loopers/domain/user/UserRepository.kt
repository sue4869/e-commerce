package com.loopers.domain.user

interface UserRepository {
    fun save(user: UserEntity)
    fun findByUserId(userId: String): UserEntity?
}
