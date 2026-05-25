package com.coffeeshop

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
class CoffeeShopApplicationTests {

    @Test
    fun contextLoads() {
        // Verifies that the Spring context starts successfully
    }
}
