package com.loopers.domain.payment

import com.loopers.interfaces.api.ApiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "pgClient",
    url = "\${pg-client.url}"
)
interface PgClient {

    /**
     * 결제 요청
     */
    @PostMapping(
        value = ["/api/v1/payments"],
        consumes = ["application/json"]
    )
    fun requestPayment(
        @RequestHeader("X-USER-ID") userId: String,
        @RequestBody request: PgRequest
    ): ApiResponse<TransactionResponse>

    /**
     * 결제 단건 조회
     */
    @GetMapping("/{transactionKey}")
    fun getPayment(
        @RequestHeader("X-USER-ID") userId: String,
        @PathVariable("transactionKey") transactionKey: String
    ): ApiResponse<TransactionDetailResponse>

    /**
     * 주문에 엮인 결제 정보 조회
     */
    @GetMapping
    fun getPaymentsByOrder(
        @RequestHeader("X-USER-ID") userId: String,
        @RequestParam("orderId") orderId: String
    ): ApiResponse<PgOfOrderResponse>

}
