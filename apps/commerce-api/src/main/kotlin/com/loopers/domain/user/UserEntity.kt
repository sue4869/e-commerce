package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table


@Entity
@Table(name = "member")
class UserEntity (
    userId: String,
    birthDate: String,
    email: String,
    gender: Gender
) : BaseEntity() {

    @Column(name = "user_id", unique = true, nullable = false)
    var userId = userId
        protected set

    @Column(name = "birth_date", nullable = false)
    var birthDate = birthDate
        protected set

    @Column(name = "email", nullable = false)
    var email = email
        protected set

    @Column(name = "gender", nullable = false)
    var gender = gender
        protected set

    companion object {
        fun of(command: UserCommand.Create): UserEntity {
            return UserEntity(
                userId = command.userId,
                birthDate = command.birth,
                email = command.email,
                gender = command.gender
            )
        }
    }
}
