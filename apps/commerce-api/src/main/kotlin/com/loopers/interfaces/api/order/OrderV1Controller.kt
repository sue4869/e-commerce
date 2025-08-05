package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderFacade
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderV1Controller(
    private val orderFacade: OrderFacade,
): OrderV1ApiSpec {

    @PostMapping
    override fun create(@RequestBody @Valid request: OrderV1Models.Request.Create, httpRequest: HttpServletRequest) : ApiResponse<Unit> {
        val userId = httpRequest.getHeader("X-USER-ID") ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "X-USER-ID is missing")
        return ApiResponse.success(orderFacade.create(request.toOrderCommand(userId), request.toPaymentCommand()))
    }
}
