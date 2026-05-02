package com.coffeeshop.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

// In dev (no nginx), Spring Boot serves uploaded files directly.
// In prod, nginx intercepts /uploads/** before reaching Spring Boot.
@Configuration
class WebMvcConfig(private val props: AppProperties) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:${props.uploads.dir}/")
        registry.addResourceHandler("/menu/**")
            .addResourceLocations("classpath:/static/menu/")
        registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/js/")
    }
}
