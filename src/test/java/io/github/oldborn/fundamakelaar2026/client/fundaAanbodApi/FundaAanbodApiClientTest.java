package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodObjectDTO;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPagingDTO;
import io.github.oldborn.fundamakelaar2026.config.FundaAanbodApiClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class FundaAanbodApiClientTest {

    private String fixture;
    private final static String API_KEY = "random-mock-api-key";
    private FundaAanbodApiClient client;

    @BeforeEach
    public void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
        this.fixture = new ClassPathResource("fixtures/funda-aanbod-api-response.json").getContentAsString(UTF_8);
        client = new FundaAanbodApiClient(
                new FundaAanbodApiClientConfig(wireMockRuntimeInfo.getHttpBaseUrl(), API_KEY),
                WebClient.builder()
        );
    }

    @Test
    public void fetchesAndParsesPageCorrectly(){
        stubFor(
                get(urlPathEqualTo("/random-mock-api-key/"))
                        .withQueryParam("type", equalTo("koop"))
                        .withQueryParam("zo", equalTo("/amsterdam/"))
                        .withQueryParam("page", equalTo("1"))
                        .withQueryParam("pagesize", equalTo("25"))
                        .willReturn(okJson(fixture))
        );

        AanbodPageDTO pageDTO = client.fetchPage(1, 25).block();
        AanbodPageDTO expected = new AanbodPageDTO(
                List.of(
                        new AanbodObjectDTO(
                                "Karspeldreef 216-1",
                                7855354L,
                                "a99813e5-cf01-49bf-8260-56ece054f859",
                                true,
                                false,
                                false,
                                950000,
                                24585,
                                "Bert van Vulpen makelaars + hypotheken Amstelveen",
                                "1102FA",
                                "http://www.funda.nl/koop/amsterdam/huis-43200717-karspeldreef-216-1/",
                                "StatusBeschikbaar",
                                "Amsterdam"
                        ),
                        new AanbodObjectDTO(
                                "Amstelveenseweg 182-3",
                                7834844L,
                                "9ddd5cc7-fabd-420a-abb0-3bb447cb169e",
                                true,
                                false,
                                false,
                                4400000,
                                24633,
                                "DAMSTAD",
                                "1075MJ",
                                "http://www.funda.nl/koop/amsterdam/huis-43289207-amstelveenseweg-182-3/",
                                "StatusBeschikbaar",
                                "Amsterdam"
                        )
                ),
                new AanbodPagingDTO(
                        29,
                        1,
                        "/~/koop/amsterdam/tuin/p2/",
                        null
                ),
                702
        );

        assertEquals(expected, pageDTO);
    }
}
