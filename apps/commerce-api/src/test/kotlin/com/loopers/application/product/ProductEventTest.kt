package com.loopers.application.product

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.dto.ProductDislikedEvent
import com.loopers.domain.dto.ProductLikedEvent
import com.loopers.domain.event.KafkaEventPublisher
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductCountService
import com.loopers.domain.product.ProductRankService
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.ProductToUserLikeService
import com.loopers.domain.user.Gender
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductEventTest {

    private val productService = mockk<ProductService>()
    private val productToUserLikeService = mockk<ProductToUserLikeService>()
    private val productCountService = mockk<ProductCountService>()
    private val userService = mockk<UserService>()
    private val productRankService = mockk<ProductRankService>()
    private val eventPublisher = mockk<EventPublisher>(relaxed = true)
    private val kafkaEventPublisher = mockk<KafkaEventPublisher>(relaxed = true)

    private lateinit var productFacade: ProductFacade

    @BeforeEach
    fun setUp() {
        productFacade = ProductFacade(
            productService,
            productToUserLikeService,
            productCountService,
            userService,
            productRankService,
            eventPublisher,
            kafkaEventPublisher,
            productLikeKafkaTopicName = "product-like-event",
            productViewKafkaTopicName = "product-view"
        )
    }

    private val testUser = UserCommand.UserInfo(
        id = 1L,
        userId = "userId",
        email = "test@example.com",
        gender = Gender.MALE,
        birth = "2000-01-01",
    )

    @Test
    fun `like 성공 시 이벤트 발행`() {
        // given
        val command = ProductCommand.Like(userId = "userId", productId = 100L)
        every { userService.findByUserId("userId") } returns testUser
        every { productToUserLikeService.create(command) } returns true

        // when
        productFacade.like(command)

        // then
        verify { eventPublisher.publish(ofType(ProductLikedEvent::class)) }
    }

    @Test
    fun `like 실패 시 이벤트 미발행`() {
        // given
        val command = ProductCommand.Like(userId = "userId", productId = 100L)
        every { userService.findByUserId("userId") } returns testUser
        every { productToUserLikeService.create(command) } returns false

        // when
        productFacade.like(command)

        // then
        verify(exactly = 0) { eventPublisher.publish(ofType(ProductDislikedEvent::class)) }
    }

    @Test
    fun `dislike 성공 시 이벤트 발행`() {
        // given
        val command = ProductCommand.Like(userId = "userId", productId = 100L)
        every { userService.findByUserId("userId") } returns testUser
        every { productToUserLikeService.delete(command) } returns true

        // when
        productFacade.dislike(command)

        // then
        verify { eventPublisher.publish(ofType(ProductDislikedEvent::class)) }
    }

    @Test
    fun `dislike 실패 시 이벤트 미발행`() {
        // given
        val command = ProductCommand.Like(userId = "userId", productId = 100L)
        every { userService.findByUserId("userId") } returns testUser
        every { productToUserLikeService.delete(command) } returns false

        // when
        productFacade.dislike(command)

        // then
        verify(exactly = 0) { eventPublisher.publish(ofType(ProductDislikedEvent::class)) }
    }
}
