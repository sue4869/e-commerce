package com.loopers.interfaces.api.point

import com.loopers.application.point.PointFacade
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/point")
class PointV1Controller(
    private val pointFacade: PointFacade
) : PointV1ApiSpec {

    @GetMapping
    override fun getPoint(request: HttpServletRequest): ApiResponse<PointV1Dto.Response.PointResponse> {
        val userId = request.getHeader("X-USER-ID") ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID is missing")
        return pointFacade.getPoint(userId).let { ApiResponse.success(it) }
    }
}
