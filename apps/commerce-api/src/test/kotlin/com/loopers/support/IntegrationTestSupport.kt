package com.loopers.support

import com.loopers.utils.DatabaseCleanUp
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class IntegrationTestSupport(
    private val databaseCleanUp: DatabaseCleanUp
) {

    @AfterEach
    fun cleanUp() {
        databaseCleanUp.truncateAllTables()
    }
}
