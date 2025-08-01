package com.loopers.domain.user

interface UserRepository {
    fun save(user: UserEntity): UserEntity
    fun findByUserId(userId: String): UserEntity?
}
