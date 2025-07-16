package com.loopers.interfaces.api.user

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "User V1 Api", description = "회원가입 및 내정보 조회")
interface UserV1ApiSpec {
    @Schema(description = "회원 가입")
    fun create(@RequestBody request: UserV1Dto.Request.SinUp): ApiResponse<UserV1Dto.Response.UserResponse>
}
