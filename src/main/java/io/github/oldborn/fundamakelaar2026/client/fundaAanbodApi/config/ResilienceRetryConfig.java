package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.config;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@ConfigurationProperties(prefix = "funda-aanbod-api-client.resilience.retry")
public class ResilienceRetryConfig {
    private final Integer maxAttempts;
    private final Integer backOffFirstAttemptInSeconds;
    private final Integer backOffMultiplier;

    private static final  Class<? extends Throwable>[] exceptions = new Class[]{
            WebClientResponseException.TooManyRequests.class,
            WebClientResponseException.InternalServerError.class,
            WebClientResponseException.ServiceUnavailable.class,
            WebClientResponseException.Unauthorized.class,
            WebClientRequestException.class,
            TimeoutException.class
    };

    public ResilienceRetryConfig(Integer maxAttempts, Integer backOffFirstAttemptInSeconds, Integer backOffMultiplier) {
        this.maxAttempts = Optional.ofNullable(maxAttempts).orElse(4);
        this.backOffFirstAttemptInSeconds = Optional.ofNullable(backOffFirstAttemptInSeconds).orElse(8);
        this.backOffMultiplier = Optional.ofNullable(backOffMultiplier).orElse(2);
    }

    @Bean("funda-retry")
    public Retry retry(){
        return Retry.of(
                "funda-api-retry",
                RetryConfig.custom()
                        .maxAttempts(maxAttempts)
                        .retryExceptions(exceptions)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(
                                Duration.ofSeconds(backOffFirstAttemptInSeconds), backOffMultiplier
                        ))
                        .build()
        );
    }

}
