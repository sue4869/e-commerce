package com.loopers.fixture.user

import com.loopers.domain.user.Gender
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserEntity
import com.loopers.interfaces.api.user.UserV1Models

sealed class UserFixture {

    abstract val userId: String
    abstract val birth: String
    abstract val email: String
    abstract val gender: Gender

    fun createUserCommand(
        userId: String = this.userId,
        birthDate: String = this.birth,
        email: String = this.email,
        gender: Gender = this.gender
    ): UserCommand.Create {
        return UserCommand.Create(
            userId = userId,
            birth = birthDate,
            email = email,
            gender = gender
        )
    }

    fun createSignUpRequest(
        userId: String = this.userId,
        birthDate: String = this.birth,
        email: String = this.email,
        gender: Gender = this.gender
    ): UserV1Models.Request.SignUp {
        return UserV1Models.Request.SignUp(
            userId = userId,
            birthDate = birthDate,
            email = email,
            gender = gender
        )
    }

    fun createUserEntity(
        userId: String = this.userId,
        birthDate: String = this.birth,
        email: String = this.email,
        gender: Gender = this.gender
    ): UserEntity {
        return UserEntity(
            userId = userId,
            birthDate = birthDate,
            email = email,
            gender
        )
    }

    object Normal : UserFixture() {
        override val userId = "user1"
        override val birth = "2020-03-12"
        override val email = "user@example.com"
        override val gender = Gender.FEMALE
    }
}

