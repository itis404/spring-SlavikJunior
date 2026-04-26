package com.coffeeshop.config

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig : CachingConfigurer {

    private val log = LoggerFactory.getLogger(CacheConfig::class.java)

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val mapper = jsonMapper {
            addModule(kotlinModule())
            activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfSubType(Any::class.java).build(),
                DefaultTyping.NON_FINAL
            )
        }
        val serializer = GenericJacksonJsonRedisSerializer(mapper)
        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeValuesWith(SerializationPair.fromSerializer<Any>(serializer))
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfig)
            .build()
    }

    override fun errorHandler(): CacheErrorHandler = object : CacheErrorHandler {
        override fun handleCacheGetError(e: RuntimeException, cache: Cache, key: Any) {
            log.warn("Cache GET error on '${cache.name}' key=$key: ${e.message}")
        }
        override fun handleCachePutError(e: RuntimeException, cache: Cache, key: Any, value: Any?) {
            log.warn("Cache PUT error on '${cache.name}' key=$key: ${e.message}")
        }
        override fun handleCacheEvictError(e: RuntimeException, cache: Cache, key: Any) {
            log.warn("Cache EVICT error on '${cache.name}' key=$key: ${e.message}")
        }
        override fun handleCacheClearError(e: RuntimeException, cache: Cache) {
            log.warn("Cache CLEAR error on '${cache.name}': ${e.message}")
        }
    }
}
