package de.bund.digitalservice.ris.search.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("allCounts");
    cacheManager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(1)).maximumSize(1000));
    return cacheManager;
  }
}
