package com.loopers.domain.user

import com.loopers.fixture.user.UserFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserJoinModelTest {

    @DisplayName("유저 모델 생성시")
    @Nested
    inner class Create {

        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패하여 CoreException이 발생한다.")
        @Test
        fun return_fail_id_length10() {
            //arrange
            val request = UserFixture.Normal.createSignUpRequest(
                userId = "121234556567"
            )

            //act
            val result = assertThrows<CoreException> {
                request.toCommand()
            }

            //assert
            assertThat(result.errorType).isEqualTo(ErrorType.INVALID_USER_ID_FORMAT)
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패하여 CoreException가 발생한다.")
        @Test
        fun return_fail_email_regex() {
            //arrange
            val request = UserFixture.Normal.createSignUpRequest(
                email = "email"
            )

            //act
            val result = assertThrows<CoreException> {
                request.toCommand()
            }

            //assert
            assertThat(result.errorType).isEqualTo(ErrorType.INVALID_USER_EMAIL_FORMAT)
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패하여 CoreException가 발생한다.")
        @Test
        fun return_fail_birth_regex() {
            //arrange
            val request = UserFixture.Normal.createSignUpRequest(
                birthDate = "2025"
            )

            //act
            val result = assertThrows<CoreException> {
                request.toCommand()
            }

            //assert
            assertThat(result.errorType).isEqualTo(ErrorType.INVALID_USER_BIRTH_DAY_FORMAT)
        }
    }
}
