package com.loopers.domain.listener

import com.loopers.domain.dto.PaidCompletedEvent
import com.loopers.domain.dto.PaidFailedEvent
import com.loopers.domain.dto.ProductDislikedEvent
import com.loopers.domain.dto.ProductLikedEvent
import com.loopers.domain.dto.StockFailedEvent
import com.loopers.infrastructure.external.DataPlatformApiClient
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DataFlatformListener(
    private val dataPlatformApiClient: DataPlatformApiClient,
) {

    private val log = KotlinLogging.logger {}

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaidCompletedEvent) {
        try {
            dataPlatformApiClient.send(event.orderUUId)
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}" }
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaidFailedEvent) {
        try {
            dataPlatformApiClient.send(event.orderUUId)
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}" }
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: StockFailedEvent) {
        try {
            dataPlatformApiClient.send(event.orderUUId)
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}" }
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: ProductLikedEvent) {
        try {
            dataPlatformApiClient.send(event.productId.toString())
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}" }
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: ProductDislikedEvent) {
        try {
            dataPlatformApiClient.send(event.productId.toString())
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}" }
        }
    }
}
