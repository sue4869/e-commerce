package com.loopers.domain.order

import com.loopers.domain.product.ProductEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class StockServiceTest {

    @InjectMocks
    lateinit var stockService: StockService

    @Test
    @DisplayName("재고가 충분하면 정상적으로 차감된다")
    fun `update_stock`() {
        // given
        val product = ProductEntity(
            name = "상품",
            brandId = 1L,
            price = BigDecimal(1000),
            stock = 10,
            likeCount = 0
        )

        // when
        stockService.updateStock(product, 3)

        // then
        assertThat(product.stock).isEqualTo(7)
    }

    @Test
    @DisplayName("재고가 부족하면 CoreException이 발생한다")
    fun `validateStock throws exception when stock is insufficient`() {

        val exception = assertThrows<CoreException> {
            stockService.validateStock(stock = 3, qty = 5)
        }

        assert(exception.errorType == ErrorType.OUT_OF_STOCK)
    }
}
