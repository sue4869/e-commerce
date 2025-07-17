package com.loopers.interfaces.api.point

import com.loopers.interfaces.api.ApiResponse
import org.springframework.web.bind.annotation.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid

@Tag(name = "Point V1 Api", description = "포인트 조회 및 충전")
interface PointV1ApiSpec {

    fun getPoint(request: HttpServletRequest): ApiResponse<PointV1Dto.Response.PointResponse>

    fun charge(
         @RequestBody @Valid request: PointV1Dto.Request.ChargeRequest, httpRequest: HttpServletRequest
     ): ApiResponse<PointV1Dto.Response.ChargeResponse>

 }
