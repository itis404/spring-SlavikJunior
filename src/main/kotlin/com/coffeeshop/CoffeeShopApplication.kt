package com.coffeeshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CoffeeShopApplication

fun main(args: Array<String>) {
    runApplication<CoffeeShopApplication>(*args)
}
