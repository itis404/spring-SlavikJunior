package com.coffeeshop.config

import org.h2.server.web.JakartaWebServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dev")
class DevConfig {

    @Bean
    fun h2ConsoleServlet(): ServletRegistrationBean<JakartaWebServlet> =
        ServletRegistrationBean(JakartaWebServlet(), "/h2-console/*").apply {
            addInitParameter("webAllowOthers", "false")
        }
}
