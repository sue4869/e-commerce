package com.loopers.interfaces.api.point

import com.loopers.application.point.PointFacade
import com.loopers.domain.point.PointCommand
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/point")
class PointV1Controller(
    private val pointFacade: PointFacade
) : PointV1ApiSpec {

    @GetMapping
    override fun getPoint(request: HttpServletRequest): ApiResponse<PointV1Dto.Response.PointResponse> {
        val userId = request.getHeader("X-USER-ID") ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "X-USER-ID is missing")
        return pointFacade.getPoint(userId).let { ApiResponse.success(it) }
    }

    @PatchMapping
    override fun charge(@RequestBody @Valid request: PointV1Dto.Request.ChargeRequest, httpRequest: HttpServletRequest): ApiResponse<PointV1Dto.Response.ChargeResponse> {
        val userId = httpRequest.getHeader("X-USER-ID") ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "X-USER-ID is missing")
        return pointFacade.charge(request.toCommand(userId)).let { ApiResponse.success(it) }
    }
}
