package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl.createDate
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

    var userId = userId
        protected set

    var birthDate = birthDate
        protected set

    var email = email
        protected set

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
