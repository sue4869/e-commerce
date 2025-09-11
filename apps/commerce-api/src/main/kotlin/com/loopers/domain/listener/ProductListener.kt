package com.loopers.domain.listener

import com.loopers.domain.dto.ProductDislikedEvent
import com.loopers.domain.dto.ProductLikedEvent
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
    fun handleAfterLike(event: ProductLikedEvent) {
        productCountService.like(event.productId)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfterDislike(event: ProductDislikedEvent) {
        productCountService.dislike(event.productId)
    }
}
