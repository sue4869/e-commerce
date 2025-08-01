package com.loopers.application.product

import com.loopers.application.point.PointFacade
import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.point.PointRepository
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductLikeRepository
import com.loopers.domain.product.ProductLikeService
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.ProductWithBrandDto
import com.loopers.domain.user.UserRepository
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import kotlin.test.Test

class ProductFacadeTest(
    private val productFacade: ProductFacade,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository,
): IntegrationTestSupport() {

    @Autowired
    private lateinit var productLikeService: ProductLikeService

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
            val likeCommand = ProductCommand.Like(createdProduct.id, createdUser.userId)

            // act
            productFacade.like(likeCommand)

            // assert
            val productLikes = productLikeService.getMyLikes(createdUser.userId)

            // 데이터베이스에서 최신 상태를 직접 조회하여 검증
            val updatedProduct = productRepository.findById(createdProduct.id)

            assertAll(
                { assertThat(productLikes).hasSize(1) },
                { assertThat(productLikes[0].userId).isEqualTo(createdUser.userId) },
                { assertThat(productLikes[0].productId).isEqualTo(createdProduct.id) },
                { assertThat(updatedProduct.likeCount).isEqualTo(1) },
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
            val likeCommand = ProductCommand.Like(createdProduct.id, createdUser.userId)

            // act
            productFacade.like(likeCommand)
            productFacade.like(likeCommand)
            productFacade.like(likeCommand)

            // assert
            val productLikes = productLikeService.getMyLikes(createdUser.userId)

            // 데이터베이스에서 최신 상태를 직접 조회하여 검증
            val updatedProduct = productRepository.findById(createdProduct.id)

            assertAll(
                { assertThat(productLikes).hasSize(1) },
                { assertThat(productLikes[0].userId).isEqualTo(createdUser.userId) },
                { assertThat(productLikes[0].productId).isEqualTo(createdProduct.id) },
                { assertThat(updatedProduct.likeCount).isEqualTo(1) },
            )
        }

        @DisplayName("상품 좋아요 취소에 성공하면 상품 좋아요 이력이 삭제된다.")
        @Test
        fun success_deleteLike() {
            // arrange
            val brandTest = BrandEntity(name = "test brand")
            val brand = brandRepository.save(brandTest)
            val productEntity = ProductFixture.Normal.create(brand.id)
            val userEntity = UserFixture.Normal.createUserEntity()
            val createdUser = userRepository.save(userEntity)
            val createdProduct = productRepository.save(productEntity)
            val likeCommand = ProductCommand.Like(createdProduct.id, createdUser.userId)

            // act
            productFacade.like(likeCommand)
            productFacade.deleteLike(likeCommand)

            // assert
            val productLikes = productLikeService.getMyLikes(createdUser.userId)
            val updatedProduct = productRepository.findById(createdProduct.id)

            assertAll(
                { assertThat(productLikes).hasSize(0) },
                { assertThat(updatedProduct.likeCount).isEqualTo(0) },
            )
        }
    }

    @DisplayName("상품")
    @Nested
    inner class Product {

        @DisplayName("상품 정보는 브랜드 정보, 좋아요수, 재고, 가격 등을 포함한다")
        @Test
        fun return_product_info () {
            // given
            val brandTest = BrandEntity(name = "test brand")
            val brand = brandRepository.save(brandTest)
            val productEntity = ProductFixture.Normal.create(brand.id)
            val createdProduct = productRepository.save(productEntity)

            // when
            val result = productFacade.get(createdProduct.id)

            // then
            assertThat(result.productId).isEqualTo(createdProduct.id)
            assertThat(result.productName).isEqualTo(createdProduct.name)
            assertThat(result.likeCount).isEqualTo(createdProduct.likeCount)
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
            val productEntity1 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(300))
            val productEntity2 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(200))
            val productEntity3 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(100))
            productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3))

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
            val command = ProductCommand.QueryCriteria(
                emptyList(), null, pageRequest)
            val productsPage = productFacade.getList(command)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(3) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(3) },
                { Assertions.assertThat(productsPage.content[0].price.compareTo(productEntity3.price)).isZero() },
                { Assertions.assertThat(productsPage.content[0].brandId).isEqualTo(productEntity3.brandId) },
                { Assertions.assertThat(productsPage.content[0].likeCount).isEqualTo(productEntity3.likeCount) },
                { Assertions.assertThat(productsPage.content[0].brandName).isEqualTo("test brand") }
            )
        }

        @DisplayName("상품 목록은 가격 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceAsc() {
            // arrange
            val brandTest = BrandEntity(name = "test brand")
            val brand = brandRepository.save(brandTest)
            val productEntity1 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(300))
            val productEntity2 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(200))
            val productEntity3 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(100))
            productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3))

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
            val command = ProductCommand.QueryCriteria(
                emptyList(), null, pageRequest)
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
            val productEntity1 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(300))
            val productEntity2 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(200))
            val productEntity3 = ProductFixture.Normal.createByPrice(brand.id, BigDecimal.valueOf(100))
            val created = productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3)).sortedByDescending { it.id }

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("latest").descending())
            val command = ProductCommand.QueryCriteria(
                emptyList(), null, pageRequest)
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
            val productEntity1 = ProductFixture.Normal.createByLikeCount(brand.id, 3)
            val productEntity2 = ProductFixture.Normal.createByLikeCount(brand.id, 2)
            val productEntity3 = ProductFixture.Normal.createByLikeCount(brand.id, 1)
            productRepository.saveAll(listOf(productEntity1, productEntity2, productEntity3))

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("likes").descending())
            val command = ProductCommand.QueryCriteria(
                emptyList(), null, pageRequest)
            val productsPage = productFacade.getList(command)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(3) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(3) },
                { Assertions.assertThat(productsPage.content[0].likeCount).isEqualTo(productEntity1.likeCount) },
                { Assertions.assertThat(productsPage.content[1].likeCount).isEqualTo(productEntity2.likeCount) },
                { Assertions.assertThat(productsPage.content[2].likeCount).isEqualTo(productEntity3.likeCount) },
            )
        }
    }

}
