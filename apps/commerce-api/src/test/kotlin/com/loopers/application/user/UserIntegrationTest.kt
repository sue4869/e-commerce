package com.loopers.application.user

import com.loopers.domain.user.UserEntity
import com.loopers.domain.user.UserRepository
import com.loopers.domain.user.UserService
import com.loopers.fixture.user.UserFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import kotlin.test.Test

class UserIntegrationTest(
    private val userFacade: UserFacade,
    private val userRepository: UserRepository,
    databaseCleanUp: DatabaseCleanUp
): IntegrationTestSupport(databaseCleanUp) {


    @DisplayName("회원가입")
    @Nested
    inner class Join {

        @Test
        @DisplayName("회원 가입시 User 저장이 수행되고 User 정보가 반환된다.")
        fun return_user_info_when_success() {
            //arrange
            val spyRepository = spy(userRepository)
            val spyUserService = UserService(spyRepository)
            val request = UserFixture.Normal.createSignUpRequest()
            val command = request.toCommand()

            //act
            val result = spyUserService.create(command)

            //assert
            assertAll(
                { assertThat(result.userId).isEqualTo(request.userId)},
                { assertThat(result.birth).isEqualTo(request.birthDate)},
                { assertThat(result.email).isEqualTo(request.email) },
                { assertThat(result.gender).isEqualTo(request.gender) },
            )
            val captor = argumentCaptor<UserEntity>()
            verify(spyRepository).save(captor.capture())
        }

        @Test
        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, CoreException(CONFLICT) 발생한다.")
        fun fail_when_duplicate_user() {
            //arrange
            val savedUser = UserFixture.Normal.createUserCommand()
            userFacade.signUp(savedUser)
            val newUser = savedUser

            //act
            val result = assertThrows<CoreException> {
                userFacade.signUp(newUser)
            }

            //assert
            assertThat(result.errorType).isEqualTo(ErrorType.CONFLICT)
        }
    }

    @DisplayName("내 정보")
    @Nested
    inner class MyInfo {

        @Test
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        fun return_user_info_when_success() {
            //arrange
            val spyRepository = spy(userRepository)
            val spyUserService = UserService(spyRepository)
            val command = UserFixture.Normal.createUserCommand()
            val savedUser = spyUserService.create(command)

            //act
            val result = spyUserService.findByUserId(savedUser.userId)

            //arrange
            assertAll(
                { assertThat(result?.userId).isEqualTo(savedUser.userId)},
                { assertThat(result?.birth).isEqualTo(savedUser.birth)},
                { assertThat(result?.email).isEqualTo(savedUser.email) },
                { assertThat(result?.gender).isEqualTo(savedUser.gender) },
            )
        }

        @Test
        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        fun return_null_when_not_exist_id() {
            //arrange
            val spyRepository = spy(userRepository)
            val spyUserService = UserService(spyRepository)
            val testId = "test1"

            //act
            val result = spyUserService.findByUserId(testId)

            //arrange
            assertThat(result).isEqualTo(null)
        }

    }
}
