package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.config.FundaAanbodApiClientConfig;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.resilience4j.core.EventConsumer;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class FundaAanbodApiClient {

    private static final Logger logger = LoggerFactory.getLogger(FundaAanbodApiClient.class);

    private final WebClient webClient;
    private final RateLimiter rateLimiter;
    private final Retry retry;

    private static final int TIMEOUT_IN_SECONDS = 5;

    public FundaAanbodApiClient(
            FundaAanbodApiClientConfig fundaAanbodApiClientConfig,
            WebClient.Builder webClientBuilder,
            RateLimiter rateLimiter,
            Retry retry
    ) {
        String baseUrl = String.format(
                "%s/%s/",
                fundaAanbodApiClientConfig.getBaseUrl(),
                fundaAanbodApiClientConfig.getApiKey()
        );
        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.rateLimiter = rateLimiter;

        this.retry = retry;

        this.retry.getEventPublisher()
                .onRetry(
                        event -> logger.warn(
                                "attempt {}/{} failed, retry in {} seconds. cause: {}",
                                event.getNumberOfRetryAttempts(),
                                retry.getRetryConfig().getMaxAttempts(),
                                event.getWaitInterval().toSeconds(),
                                extractExceptionMessage(event.getLastThrowable())
                        )
                );
    }

    private static String extractExceptionMessage(Throwable exception){
        if (exception instanceof WebClientResponseException){
            int statusCode = ((WebClientResponseException) exception).getStatusCode().value();
            String statusText = ((WebClientResponseException) exception).getStatusText();
            return String.format("HTTP Call failed with status code: %d status message: %s", statusCode, statusText);
        }

        // To avoid risk of logging full URL which contains API_KEY
        return exception.getClass().getSimpleName();
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
                .timeout(Duration.ofSeconds(TIMEOUT_IN_SECONDS))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .transformDeferred(RetryOperator.of(retry));
    }
}
