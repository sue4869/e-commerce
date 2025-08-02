package com.loopers.interfaces.api.order

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest

@Tag(name = "Order V1 Api", description = "주문")
interface OrderV1ApiSpec {

    fun create(request: OrderV1Models.Request.Create, httpRequest: HttpServletRequest) : ApiResponse<Unit>
}
