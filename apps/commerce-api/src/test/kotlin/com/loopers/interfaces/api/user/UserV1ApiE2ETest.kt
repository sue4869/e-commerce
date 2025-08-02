package com.loopers.interfaces.api.user

import com.loopers.domain.user.UserService
import com.loopers.fixture.user.UserFixture
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.E2ETestSupport
import org.springframework.http.HttpHeaders
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import kotlin.test.Test

class UserV1ApiE2ETest(
    private val userService: UserService,
) : E2ETestSupport() {

    companion object {
        private val ENDPOINT_JOIN = "/api/v1/users"
        private val ENDPOINT_ME = "/api/v1/users/me"
    }

    @DisplayName("회원가입")
    @Nested
    inner class Join {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        fun create_User_after_return_userInfo() {
            //arrange
            val request = UserFixture.Normal.createSignUpRequest()

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Models.Response.Info>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_JOIN, HttpMethod.POST, HttpEntity(request), responseType)

            //assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue },
                { assertThat(response.body?.data?.userId).isEqualTo(request.userId)},
                { assertThat(response.body?.data?.birthDate).isEqualTo(request.birthDate)},
                { assertThat(response.body?.data?.email).isEqualTo(request.email) },
            )

        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        fun return_400_BadRequest_When_gender_null() {
            //arrange
            val request = """
                {
                    "userId": "1234",
                    "email": "email@naver.com",
                    "birthDate": "2025-07-14"
                }
            """.trimIndent()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Models.Response.Info>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_JOIN, HttpMethod.POST, httpEntity, responseType)

            //assert
            assertThat(response.statusCode.is4xxClientError).isTrue()
        }
    }

    @DisplayName("내 정보")
    @Nested
    inner class MyInfo {

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        fun create_User_after_return_userInfo() {
            //arrange
            val savedUserCommand = UserFixture.Normal.createUserCommand()
            val savedUser = userService.create(savedUserCommand)

            //act
            val headers = headersWithUserId( savedUserCommand.userId)
            val httpEntity = HttpEntity(null, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Models.Response.Info>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_ME, HttpMethod.GET, httpEntity, responseType)

            //assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue },
                { assertThat(response.body?.data?.userId).isEqualTo(savedUser.userId)},
                { assertThat(response.body?.data?.birthDate).isEqualTo(savedUser.birth)},
                { assertThat(response.body?.data?.email).isEqualTo(savedUser.email) },
            )
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        fun return_404_when_not_exist_id() {
            //arrange
            val testId = "test1"

            //act
            val headers = headersWithUserId(testId)
            val httpEntity = HttpEntity(null, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Models.Response.Info>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_ME, HttpMethod.GET, httpEntity, responseType)

            //assert
            assertThat(response.statusCode.is4xxClientError).isTrue()
        }
    }
}
