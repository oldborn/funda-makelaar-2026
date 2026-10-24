package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class FundaAanbodApiClient {

    private static final Logger logger = LoggerFactory.getLogger(FundaAanbodApiClient.class);

    private final WebClient webClient;

    private final RateLimiter rateLimiter;
    private final Retry retry;

    public FundaAanbodApiClient(
            FundaAanbodApiClientConfig fundaAanbodApiClientConfig,
            WebClient.Builder webClientBuilder
    ) {
        String baseUrl = String.format(
                "%s/%s/",
                fundaAanbodApiClientConfig.getBaseUrl(),
                fundaAanbodApiClientConfig.getApiKey()
        );
        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.rateLimiter = RateLimiter.of(
                "funda-api-rate-limiter",
                RateLimiterConfig.custom()
                        .limitRefreshPeriod(Duration.ofSeconds(65))
                        .limitForPeriod(100)
                        .timeoutDuration(Duration.ofSeconds(67))
                        .build()
        );

        this.retry = Retry.of(
                "funda-api-retry",
                RetryConfig.custom()
                        .maxAttempts(4)
                        .retryExceptions(
                                WebClientResponseException.TooManyRequests.class,
                                WebClientResponseException.InternalServerError.class,
                                WebClientResponseException.ServiceUnavailable.class,
                                WebClientResponseException.Unauthorized.class,
                                WebClientRequestException.class,
                                TimeoutException.class
                        )
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(
                                Duration.ofSeconds(8), 2
                        ))
                        .build()
        );

        this.retry.getEventPublisher()
                .onRetry(
                        event -> logger.warn(
                                "attempt {}/{} failed, retry in {} seconds. cause: {}",
                                event.getNumberOfRetryAttempts(),
                                4,
                                event.getWaitInterval().toSeconds(),
                                event.getLastThrowable().getMessage()
                        )
                );
    }

    public Mono<AanbodPageDTO> fetchPage(
            int page,
            int pageSize,
            String city,
            @Nullable String filter
    ){
        String zo = filter == null ? String.format("/%s/", city) : String.format("/%s/%s/", city, filter);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("type", "koop")
                        .queryParam("zo", zo)
                        .queryParam("page", page)
                        .queryParam("pagesize", pageSize)
                        .build()
                )
                .retrieve()
                .bodyToMono(AanbodPageDTO.class)
                .timeout(Duration.ofSeconds(2))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .transformDeferred(RetryOperator.of(retry));
    }
}
