package com.management.water.telemetry.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String WATER_RATES_CACHE = "waterRates";
    public static final String APARTMENT_CONFIGS_CACHE = "apartmentConfigs";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(WATER_RATES_CACHE, APARTMENT_CONFIGS_CACHE);
    }
}
