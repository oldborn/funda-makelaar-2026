package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class FundaAanbodApiClient {
    private final WebClient webClient;

    private final RateLimiter rateLimiter;

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
                        .limitForPeriod(50)
                        .timeoutDuration(Duration.ofSeconds(67))
                        .build()
        );
    }

    public Mono<AanbodPageDTO> fetchPage(int page, int pageSize){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("type", "koop")
                        .queryParam("zo", "/amsterdam/")
                        .queryParam("page", page)
                        .queryParam("pagesize", pageSize)
                        .build()
                )
                .retrieve()
                .bodyToMono(AanbodPageDTO.class)
                .transformDeferred(RateLimiterOperator.of(rateLimiter));
    }
}
