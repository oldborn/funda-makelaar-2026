package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.Optional;

@ConfigurationProperties(prefix = "funda-aanbod-api-client.resilience.ratelimiter")
public class ResilienceRateLimiterConfig {
    private final Integer limitRefreshPeriod;
    private final Integer limitForPeriod;
    private final Integer timeoutDurationInSeconds;

    public ResilienceRateLimiterConfig(Integer limitRefreshPeriod, Integer limitForPeriod, Integer timeoutDurationInSeconds) {
        this.limitRefreshPeriod = Optional.ofNullable(limitRefreshPeriod).orElse(65);
        this.limitForPeriod = Optional.ofNullable(limitForPeriod).orElse(100);
        this.timeoutDurationInSeconds = Optional.ofNullable(timeoutDurationInSeconds).orElse(67);
    }

    @Bean("funda-rate-limiter")
    public RateLimiter rateLimiter(){
        return RateLimiter.of(
                "funda-api-rate-limiter",
                RateLimiterConfig.custom()
                        .limitRefreshPeriod(Duration.ofSeconds(limitRefreshPeriod))
                        .limitForPeriod(limitForPeriod)
                        .timeoutDuration(Duration.ofSeconds(timeoutDurationInSeconds))
                        .build()
        );
    }
}
