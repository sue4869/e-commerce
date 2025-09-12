package com.loopers.application.product

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.dto.ProductDislikedEvent
import com.loopers.domain.dto.ProductKafkaEvent
import com.loopers.domain.dto.ProductLikedEvent
import com.loopers.domain.event.EventType
import com.loopers.domain.event.KafkaEventPublisher
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductCountService
import com.loopers.domain.product.ProductRankService
import com.loopers.domain.product.ProductToUserLikeService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.product.ProductV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional(readOnly = true)
@Component
class ProductFacade(
    private val productService: ProductService,
    private val productToUserLikeService: ProductToUserLikeService,
    private val productCountService: ProductCountService,
    private val userService: UserService,
    private val productRankService: ProductRankService,
    private val eventPublisher: EventPublisher,
    private val kafkaEventPublisher: KafkaEventPublisher,
    @Value("\${application.kafka-topic.product-like-event:product-like-event}") private val productLikeKafkaTopicName: String,
    @Value("\${application.kafka-topic.product-view:product-view}") private val productViewKafkaTopicName: String,
) {

    private val log = KotlinLogging.logger {}

    fun get(productId: Long): ProductV1Models.Response.GetInfo {
        val source = productService.getWithBrand(productId)
        val countDto = productCountService.getByProductId(productId)
        publishKafka(productId, EventType.PRODUCT_VIEW, productViewKafkaTopicName)
        return ProductV1Models.Response.GetInfo.of(source, countDto)
    }

    fun getList(command: ProductCommand.QueryCriteria): Page<ProductV1Models.Response.GetList> {
        val sourcePage = productService.getList(command)
        return sourcePage.map { ProductV1Models.Response.GetList.of(it) }
    }

    fun getRankingDaily(command: ProductCommand.RankingDaily): Page<ProductV1Models.Response.GetRank> {
        val sourcePage = productRankService.getRankingDaily(command)
        return sourcePage.map { ProductV1Models.Response.GetRank.of(it) }
    }

    @Transactional
    fun like(command: ProductCommand.Like) {
        userService.findByUserId(command.userId)
            ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "존재하지 않는 사용자 ID 입니다. 사용자 ID: ${command.userId}")
        if (productToUserLikeService.create(command)) {
            eventPublisher.publish(ProductLikedEvent(command.productId))
            log.info("publish ProductLikedEvent productId: ${command.productId}")
            publishKafka(command.productId, EventType.PRODUCT_LIKED, productLikeKafkaTopicName)
        }
    }

    @Transactional
    fun dislike(command: ProductCommand.Like) {
        userService.findByUserId(command.userId)
            ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID, "존재하지 않는 사용자 ID 입니다. 사용자 ID: ${command.userId}")
        if (productToUserLikeService.delete(command)) {
            eventPublisher.publish(ProductDislikedEvent(command.productId))
            log.info("publish ProductDislikedEvent productId: ${command.productId}")
            publishKafka(command.productId, EventType.PRODUCT_UNLIKED, productLikeKafkaTopicName)
        }
    }

    private fun publishKafka(productId: Long, eventType: EventType, topicName: String) {
        val messageKey = buildString {
            append(productId.toString())
            append(eventType.name)
            append(UUID.randomUUID())
        }

        kafkaEventPublisher.send(
            MessageBuilder
                .withPayload(
                    ProductKafkaEvent(
                        productId = productId,
                        event = eventType,
                        message = messageKey,
                    )
                )
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.KEY, productId)
                .build()
        )
    }
}
