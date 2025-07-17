package com.loopers.interfaces.api.point

import com.loopers.interfaces.api.ApiResponse
import com.loopers.interfaces.api.user.UserV1Dto
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest

@Tag(name = "Point V1 Api", description = "포인트 조회 및 충전")
interface PointV1ApiSpec {

    fun getPoint(request: HttpServletRequest): ApiResponse<PointV1Dto.Response.PointResponse>
}
