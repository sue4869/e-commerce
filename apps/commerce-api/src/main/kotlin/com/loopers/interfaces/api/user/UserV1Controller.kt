package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserV1Controller(
    private val userFacade: UserFacade
): UserV1ApiSpec {

    @PostMapping
    override fun create(@RequestBody @Valid request: UserV1Dto.Request.SinUp): ApiResponse<UserV1Dto.Response.UserResponse> {
        return userFacade.signUp(request.toCommand())
            .let { UserV1Dto.Response.UserResponse.of(it)}
            .let { ApiResponse.success(it) }
    }
}
