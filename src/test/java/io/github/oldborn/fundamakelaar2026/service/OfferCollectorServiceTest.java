package io.github.oldborn.fundamakelaar2026.service;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.FundaAanbodApiClient;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodObjectDTO;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPagingDTO;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class OfferCollectorServiceTest {
    private final FundaAanbodApiClient fundaAanbodApiClient = Mockito.mock(FundaAanbodApiClient.class);
    private final OfferCollectorService offerCollectorService = new OfferCollectorService(fundaAanbodApiClient);

    @Test
    public void collectAllOffers_iteratesOverAllPages_andMapsDTOtoModel(){
        AanbodPageDTO firstPage = new AanbodPageDTO(
                List.of(
                        createPartialAanbodObject("a99813e5-cf01-49bf-8260-56ece054f859", 24585, "Bernard"),
                        createPartialAanbodObject("a99813e5-cf01-49bf-8260-56ece054f832", null, "Null Makelaar")
                ),
                new AanbodPagingDTO(2, 1, "/~/koop/amsterdam/tuin/p2/", null),
                702
        );

        AanbodPageDTO secondPage = new AanbodPageDTO(
                List.of(
                        createPartialAanbodObject("9ddd5cc7-fabd-420a-abb0-3bb447cb169e", 24633, "Frank"),
                        createPartialAanbodObject("a99813e5-cf01-49bf-8260-56ece054f859", 24585, "Bernard")
                ),
                new AanbodPagingDTO(2, 1, "/~/koop/amsterdam/tuin/p2/", null),
                702
        );

        when(fundaAanbodApiClient.fetchPage(1, 25, "amsterdam", null)).thenReturn(Mono.just(firstPage));
        when(fundaAanbodApiClient.fetchPage(2, 25, "amsterdam", null)).thenReturn(Mono.just(secondPage));

        List<Offer> offers = offerCollectorService.collectAllOffers("amsterdam", null);

        Offer expectedOffer1 = new Offer(
                "a99813e5-cf01-49bf-8260-56ece054f859",
                24585,
                "Bernard"
        );
        Offer expectedOffer2 = new Offer(
                "9ddd5cc7-fabd-420a-abb0-3bb447cb169e",
                24633,
                "Frank"
        );

        assertNotNull(offers);
        assertEquals(2, offers.size());
        assertTrue(offers.containsAll(List.of(expectedOffer1, expectedOffer2)));
    }

    /*
    * A helper method to partially create an AanbodObject
    * */
    private static AanbodObjectDTO createPartialAanbodObject(String id, Integer makelaarId, String makelaarNaam){
        return new AanbodObjectDTO(
                null,
                1L,
                id,
                true,
                true,
                true,
                0,
                makelaarId,
                makelaarNaam,
                "123",
                "url",
                "true",
                "amsterdam"
        );
    }
}