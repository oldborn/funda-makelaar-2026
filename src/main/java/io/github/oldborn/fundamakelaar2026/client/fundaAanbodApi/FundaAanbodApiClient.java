package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.oldborn.fundamakelaar2026.config.FundaAanbodApiClientConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FundaAanbodApiClient {
    private final WebClient webClient;

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
                .bodyToMono(AanbodPageDTO.class);
    }
}
