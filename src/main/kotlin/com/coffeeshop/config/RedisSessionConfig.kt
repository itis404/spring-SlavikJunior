package com.coffeeshop.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

/** Enable Redis-backed HTTP sessions only in production. Dev uses in-memory sessions. */
@Configuration
@Profile("prod")
@EnableRedisHttpSession
class RedisSessionConfig
