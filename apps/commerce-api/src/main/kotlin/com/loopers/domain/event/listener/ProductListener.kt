package com.loopers.domain.event.listener

import com.loopers.domain.event.dto.ProductDislikeEvent
import com.loopers.domain.event.dto.ProductLikeEvent
import com.loopers.domain.product.ProductCountService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class ProductListener(
    private val productCountService: ProductCountService,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfterLike(event: ProductLikeEvent) {
        productCountService.like(event.productId)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfterDislike(event: ProductDislikeEvent) {
        productCountService.dislike(event.productId)
    }
}
