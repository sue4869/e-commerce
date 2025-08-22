package com.loopers.application.product

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductCountEntity
import com.loopers.domain.product.ProductCountRepository
import com.loopers.domain.product.ProductToUserLikeService
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.ProductToUserLikeEntity
import com.loopers.domain.product.ProductToUserLikeRepository
import com.loopers.domain.user.UserRepository
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.support.IntegrationTestSupport
import mu.KotlinLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import kotlin.test.Test

class ProductFacadeTest(
    private val productFacade: ProductFacade,
    private val productRepository: ProductRepository,
    private val productCountRepository: ProductCountRepository,
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository,
    private val productToUserLikeService: ProductToUserLikeService,
    private val productToUserLikeRepository: ProductToUserLikeRepository
): IntegrationTestSupport() {

    private val log = KotlinLogging.logger {}

    @DisplayName("좋아요")
    @Nested
    inner class LIKE {

        @DisplayName("상품 좋아요 등록에 성공하면 상품 좋아요 수가 증가한다.")
        @Test
        fun success_like() {
            // arrange
            val brandTest = BrandEntity(name = "test brand")
            val brand = brandRepository.save(brandTest)
            val productEntity = ProductFixture.Normal.create(brand.id)
            val userEntity = UserFixture.Normal.createUserEntity()
            val createdUser = userRepository.save(userEntity)
            val createdProduct = productRepository.save(productEntity)
            productCountRepository.save(ProductCountEntity(productId = createdProduct.id, likeCount = 0))
            val likeCommand = ProductCommand.Like(createdProduct.id, createdUser.userId)

            // act
            productFacade.like(likeCommand)

            // assert
            val productLikes = productToUserLikeService.getMyLikes(createdUser.userId)

            // 데이터베이스에서 최신 상태를 직접 조회하여 검증
            val updatedProduct = productRepository.findById(createdProduct.id)
            val updatedProductCount = productCountRepository.getByProductId(createdProduct.id)

            assertAll(
                { assertThat(productLikes).hasSize(1) },
                { assertThat(productLikes[0].userId).isEqualTo(createdUser.userId) },
                { assertThat(productLikes[0].productId).isEqualTo(updatedProduct.id) },
                { assertThat(updatedProductCount.likeCount).isEqualTo(1) },
            )
        }

        @DisplayName("상품 좋아요 여러번 할 경우 상태는 한번만 등록한것과 동일하다.")
        @Test
        fun duplicate_likeProduct() {
            // arrange
            val brandTest = BrandEntity(name = "test brand")
            val brand = brandRepository.save(brandTest)
            val productEntity = ProductFixture.Normal.create(brand.id)
            val userEntity = UserFixture.Normal.createUserEntity()
            val createdUser = userRepository.save(userEntity)
            val createdProduct = productRepository.save(productEntity)
            productCountRepository.save(ProductCountEntity(productId = createdProduct.id, likeCount = 0))
            val likeCommand = ProductCommand.Like(createdProduct.id, createdUser.userId)

            // act
            productFacade.like(likeCommand)
            productFacade.like(likeCommand)
            productFacade.like(likeCommand)

            // assert
            val productLikes = productToUserLikeService.getMyLikes(createdUser.userId)

            // 데이터베이스에서 최신 상태를 직접 조회하여 검증
            val updatedProduct = productRepository.findById(createdProduct.id)
            val updatedProductCount = productCountRepository.getByProductId(createdProduct.id)

            assertAll(
                { assertThat(productLikes).hasSize(1) },
                { assertThat(productLikes[0].userId).isEqualTo(createdUser.userId) },
                { assertThat(productLikes[0].productId).isEqualTo(updatedProduct.id) },
                { assertThat(updatedProductCount.likeCount).isEqualTo(1) },
            )
        }

        @DisplayName("상품 좋아요 취소에 성공하면 상품 좋아요가 삭제된다.")
        @Test
        fun success_deleteLike() {
            // arrange
            val brandTest = BrandEntity(name = "test brand")
            val brand = brandRepository.save(brandTest)
            val productEntity = ProductFixture.Normal.create(brand.id)
            val userEntity = UserFixture.Normal.createUserEntity()
            val createdUser = userRepository.save(userEntity)
            val createdProduct = productRepository.save(productEntity)
            productCountRepository.save(ProductCountEntity(productId = createdProduct.id, likeCount = 0))
            val likeCommand = ProductCommand.Like(createdProduct.id, createdUser.userId)

            // act
            productFacade.like(likeCommand)
            productFacade.dislike(likeCommand)

            // assert
            val productLikes = productToUserLikeService.getMyLikes(createdUser.userId)
            val updatedProductCount = productCountRepository.getByProductId(createdProduct.id)

            assertAll(
                { assertThat(productLikes).hasSize(0) },
                { assertThat(updatedProductCount.likeCount).isEqualTo(0) },
            )
        }

        @Test
        fun `동시에 like 요청이 와도 카운트가 정확하게 증가한다`() {

            fun `동시에 여러 사용자가 같은 상품에 좋아요 요청이 와도 카운트가 정확하게 증가한다`() {
                // given
                val productId = 1L
                val users = (1..10).map {
                    userRepository.save(UserFixture.Normal.createUserEntity(userId = "user$it"))
                }
                val threadCount = users.size
                productCountRepository.save(ProductCountEntity(productId))

                //when
                runConcurrentWithIndex(threadCount) { index ->
                    val user = users[index]
                    try {
                        productFacade.like(ProductCommand.Like(productId, user.userId))
                    } catch (e: Exception) {
                        log.info("예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                    }
                }

                // then
                val productCount = productCountRepository.getByProductId(productId)
                assertThat(productCount.likeCount).isEqualTo(threadCount)
            }

            @Test
            fun `동시에 여러 사용자가 같은 상품에 좋아요 취소 요청이 와도 카운트가 정확하게 감소한다`() {
                // given
                val productId = 1L
                val users = (1..10).map {
                    val userId = "user$it"
                    userRepository.save(UserFixture.Normal.createUserEntity(userId = userId))
                    ProductToUserLikeEntity.of(ProductCommand.Like(productId, userId)).apply { productToUserLikeRepository.save(this) }
                }
                val threadCount = users.size
                productCountRepository.save(ProductCountEntity(productId = productId, likeCount = 100))

                //when
                runConcurrentWithIndex(threadCount) { index ->
                    val user = users[index]
                    try {
                        productFacade.dislike(ProductCommand.Like(productId, user.userId))
                    } catch (e: Exception) {
                        log.info("예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                    }
                }

                // then
                val productCount = productCountRepository.getByProductId(productId)
                log.info("좋아요 수 : $productCount.likeCount")
                assertThat(productCount.likeCount).isEqualTo(100 - threadCount)

            }

            @Test
            fun `동시에 여러 사용자가 좋아요와 좋아요 취소를 섞어 요청해도 카운트가 정확히 반영된다`() {
                // given
                val productId = 1L
                val totalUsers = 20
                val users = (1..totalUsers).map {
                    userRepository.save(UserFixture.Normal.createUserEntity(userId = "user$it"))
                }
                // 좋아요 초기값 10으로 세팅
                productCountRepository.save(ProductCountEntity(productId = productId, likeCount = 10))
                // 좋아요 취소 할 사용자만 미리 좋아요 상태 만들어주기
                users.filterIndexed { index, _ -> index % 2 == 1 }.forEach { user ->
                    val likeEntity = ProductToUserLikeEntity.of(ProductCommand.Like(productId, user.userId))
                    productToUserLikeRepository.save(likeEntity)
                }

                // when
                runConcurrentWithIndex(totalUsers) { index ->
                    val user = users[index]
                    try {
                        if (index % 2 == 0) {
                            // 짝수 인덱스는 좋아요
                            productFacade.like(ProductCommand.Like(productId, user.userId))
                        } else {
                            // 홀수 인덱스는 좋아요 취소
                            productFacade.dislike(ProductCommand.Like(productId, user.userId))
                        }
                    } catch (e: Exception) {
                        log.info("예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                    }
                }

                // then
                val productCount = productCountRepository.getByProductId(productId)
                log.info("최종 likeCount = ${productCount.likeCount}")

                // 좋아요 요청 10개, 취소 요청 10개 중 절반씩 실행됐으니 최종 카운트는 10으로 유지돼야 함
                // 짝수 인덱스 10개가 like, 홀수 인덱스 10개가 dislike 요청이므로 10 + 10 - 10 = 10
                assertThat(productCount.likeCount).isEqualTo(10)
            }
        }
    }

        @DisplayName("상품")
        @Nested
        inner class Product {

            @DisplayName("상품 정보는 브랜드 정보, 좋아요수, 재고, 가격 등을 포함한다")
            @Test
            fun return_product_info() {
                // given
                val brandTest = BrandEntity(name = "test brand")
                val brand = brandRepository.save(brandTest)
                val productEntity = ProductFixture.Normal.create(brand.id)
                val createdProduct = productRepository.save(productEntity)
                val createdProductCount = productCountRepository.save(ProductCountEntity(productId = createdProduct.id, likeCount = 0))

                // when
                val result = productFacade.get(createdProduct.id)

                // then
                assertThat(result.productId).isEqualTo(createdProduct.id)
                assertThat(result.productName).isEqualTo(createdProduct.name)
                assertThat(result.likeCount).isEqualTo(createdProductCount.likeCount)
                assertThat(result.brandId).isEqualTo(createdProduct.brandId)
                assertThat(result.brandName).isEqualTo("test brand")
                assertThat(result.stock).isEqualTo(createdProduct.stock)
                assertThat(result.price.compareTo(createdProduct.price)).isZero()
            }

            @DisplayName("상품 목록은 브랜드 정보, 좋아요수, 재고, 가격 등을 포함한다")
            @Test
            fun returnsProductList() {
                // arrange
                val brandTest = BrandEntity(name = "test brand")
                val brand = brandRepository.save(brandTest)
                val productEntity1 = ProductFixture.Normal.createByPrice(brand.id, 300L)
                val productEntity2 = ProductFixture.Normal.createByPrice(brand.id, 200L)
                val productEntity3 = ProductFixture.Normal.createByPrice(brand.id, 100L)
                productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3))
                val productCount1 = productCountRepository.save(ProductCountEntity(productId = productEntity1.id, likeCount = 0))
                val productCount2 = productCountRepository.save(ProductCountEntity(productId = productEntity2.id, likeCount = 0))
                val productCount3 = productCountRepository.save(ProductCountEntity(productId = productEntity3.id, likeCount = 0))
                productCountRepository.saveAll(listOf(productCount1, productCount2, productCount3))

                // act
                val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
                val command = ProductCommand.QueryCriteria(
                    emptyList(), null, pageRequest
                )
                val productsPage = productFacade.getList(command)

                // assert
                assertAll(
                    { Assertions.assertThat(productsPage).hasSize(3) },
                    { Assertions.assertThat(productsPage.totalElements).isEqualTo(3) },
                    { Assertions.assertThat(productsPage.content[0].price.compareTo(productEntity3.price)).isZero() },
                    { Assertions.assertThat(productsPage.content[0].brandId).isEqualTo(productEntity3.brandId) },
                    { Assertions.assertThat(productsPage.content[0].likeCount).isEqualTo(productCount3.likeCount) },
                    { Assertions.assertThat(productsPage.content[0].brandName).isEqualTo("test brand") }
                )
            }

            @DisplayName("상품 목록은 가격 오름차순으로 정렬할 수 있다.")
            @Test
            fun returnsProductsSortedByPriceAsc() {
                // arrange
                val brandTest = BrandEntity(name = "test brand")
                val brand = brandRepository.save(brandTest)
                val productEntity1 = ProductFixture.Normal.createByPrice(brand.id, 300L)
                val productEntity2 = ProductFixture.Normal.createByPrice(brand.id, 200L)
                val productEntity3 = ProductFixture.Normal.createByPrice(brand.id, 100L)
                productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3))
                val productCount1 = productCountRepository.save(ProductCountEntity(productId = productEntity1.id, likeCount = 0))
                val productCount2 = productCountRepository.save(ProductCountEntity(productId = productEntity2.id, likeCount = 0))
                val productCount3 = productCountRepository.save(ProductCountEntity(productId = productEntity3.id, likeCount = 0))
                productCountRepository.saveAll(listOf(productCount1, productCount2, productCount3))

                // act
                val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
                val command = ProductCommand.QueryCriteria(
                    emptyList(), null, pageRequest
                )
                val productsPage = productFacade.getList(command)

                // assert
                assertAll(
                    { Assertions.assertThat(productsPage).hasSize(3) },
                    { Assertions.assertThat(productsPage.totalElements).isEqualTo(3) },
                    { Assertions.assertThat(productsPage.content[0].price.compareTo(productEntity3.price)).isZero() },
                    { Assertions.assertThat(productsPage.content[1].price.compareTo(productEntity2.price)).isZero() },
                    { Assertions.assertThat(productsPage.content[2].price.compareTo(productEntity1.price)).isZero() },
                )
            }

            @DisplayName("상품 목록은 최신순으로 정렬할 수 있다.")
            @Test
            fun returnsProductsSortedBylatest() {
                // arrange
                val brandTest = BrandEntity(name = "test brand")
                val brand = brandRepository.save(brandTest)
                val productEntity1 = ProductFixture.Normal.createByPrice(brand.id, 300L)
                val productEntity2 = ProductFixture.Normal.createByPrice(brand.id, 200L)
                val productEntity3 = ProductFixture.Normal.createByPrice(brand.id, 100L)
                val created = productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3)).sortedByDescending { it.id }
                val productCount1 = productCountRepository.save(ProductCountEntity(productId = productEntity1.id, likeCount = 0))
                val productCount2 = productCountRepository.save(ProductCountEntity(productId = productEntity2.id, likeCount = 0))
                val productCount3 = productCountRepository.save(ProductCountEntity(productId = productEntity3.id, likeCount = 0))
                productCountRepository.saveAll(listOf(productCount1, productCount2, productCount3))

                // act
                val pageRequest = PageRequest.of(0, 10, Sort.by("latest").descending())
                val command = ProductCommand.QueryCriteria(
                    emptyList(), null, pageRequest
                )
                val productsPage = productFacade.getList(command)

                // assert
                assertAll(
                    { Assertions.assertThat(productsPage).hasSize(3) },
                    { Assertions.assertThat(productsPage.totalElements).isEqualTo(3) },
                    { Assertions.assertThat(productsPage.content[0].productId).isEqualTo(created[0].id) },
                    { Assertions.assertThat(productsPage.content[1].productId).isEqualTo(created[1].id) },
                    { Assertions.assertThat(productsPage.content[2].productId).isEqualTo(created[2].id) },
                )
            }

            @DisplayName("상품 목록은 좋아요 내림차순으로 정렬할 수 있다.")
            @Test
            fun returnsProductsSortedByLikeDescending() {
                // arrange
                val brandTest = BrandEntity(name = "test brand")
                val brand = brandRepository.save(brandTest)
                val productEntity1 = ProductFixture.Normal.create(brand.id)
                val productEntity2 = ProductFixture.Normal.create(brand.id)
                val productEntity3 = ProductFixture.Normal.create(brand.id)
                productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3))
                val productCount1 = productCountRepository.save(ProductCountEntity(productId = productEntity1.id, likeCount = 3))
                val productCount2 = productCountRepository.save(ProductCountEntity(productId = productEntity2.id, likeCount = 2))
                val productCount3 = productCountRepository.save(ProductCountEntity(productId = productEntity3.id, likeCount = 1))
                productCountRepository.saveAll(listOf(productCount1, productCount2, productCount3))


                // act
                val pageRequest = PageRequest.of(0, 10, Sort.by("likes").descending())
                val command = ProductCommand.QueryCriteria(
                    emptyList(), null, pageRequest
                )
                val productsPage = productFacade.getList(command)

                // assert
                assertAll(
                    { Assertions.assertThat(productsPage).hasSize(3) },
                    { Assertions.assertThat(productsPage.totalElements).isEqualTo(3) },
                    { Assertions.assertThat(productsPage.content[0].likeCount).isEqualTo(productCount1.likeCount) },
                    { Assertions.assertThat(productsPage.content[1].likeCount).isEqualTo(productCount2.likeCount) },
                    { Assertions.assertThat(productsPage.content[2].likeCount).isEqualTo(productCount3.likeCount) },
                )
            }
        }
}
