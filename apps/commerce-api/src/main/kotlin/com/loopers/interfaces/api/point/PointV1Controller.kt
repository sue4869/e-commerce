package com.loopers.interfaces.api.point

import com.loopers.interfaces.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/point")
class PointV1Controller() : PointV1ApiSpec {

    @GetMapping()
    override fun getPoint(request: HttpServletRequest): ApiResponse<PointV1Dto.Response.PointResponse> {
        val userId = request.getHeader("X-USER-ID") ?: throw IllegalStateException("X-USER-ID not set")
        return ApiResponse.success(PointV1Dto.Response.PointResponse(
            userId = userId,
            amount = BigDecimal.valueOf(23L)
        ))
    }
}
