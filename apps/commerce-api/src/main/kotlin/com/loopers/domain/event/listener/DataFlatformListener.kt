package com.loopers.domain.event.listener

import com.loopers.domain.event.dto.PaidCompleteEvent
import com.loopers.domain.event.dto.PaidFailEvent
import com.loopers.domain.event.dto.ProductDislikeEvent
import com.loopers.domain.event.dto.ProductLikeEvent
import com.loopers.domain.event.dto.StockFailedEvent
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
    fun handle(event: PaidCompleteEvent) {
        try {
            dataPlatformApiClient.send(event.orderUUId)
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}"}
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaidFailEvent) {
        try {
            dataPlatformApiClient.send(event.orderUUId)
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}"}
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: StockFailedEvent) {
        try {
            dataPlatformApiClient.send(event.orderUUId)
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}"}
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: ProductLikeEvent) {
        try {
            dataPlatformApiClient.send(event.productId.toString())
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}"}
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: ProductDislikeEvent) {
        try {
            dataPlatformApiClient.send(event.productId.toString())
        } catch (e: Exception) {
            log.error { "데이터플랫폼 전송 실패 event: $event, error: ${e.message}"}
        }
    }
}
