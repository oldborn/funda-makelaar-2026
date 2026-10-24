package io.github.oldborn.fundamakelaar2026.shell;

import io.github.oldborn.fundamakelaar2026.service.OfferCollectorService;
import io.github.oldborn.fundamakelaar2026.service.RealtorRankingService;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import io.github.oldborn.fundamakelaar2026.service.model.Realtor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

class FundaMakelaarCommandsTest {

    private final OfferCollectorService offerCollectorService = Mockito.mock(OfferCollectorService.class);
    private RealtorRankingService realtorRankingService = Mockito.mock(RealtorRankingService.class);

    private final FundaMakelaarCommands fundaMakelaarCommands = new FundaMakelaarCommands(
            offerCollectorService,
            realtorRankingService
    );

    @Test
    public void topRealtors_shouldPassCityAndFilterCorrectly_andReturnFormattedTable(){
        String city = "random-city";
        String filter = "random-filter";
        int topN = 2;

        List<Offer> offers = Mockito.mock();

        when(offerCollectorService.collectAllOffers(city, filter)).thenReturn(offers);
        when(realtorRankingService.topRealtors(offers, topN)).thenReturn(
                List.of(
                        new Realtor(100001, "Hank", 100),
                        new Realtor(100003, "Jane", 22)
                )
        );

        String result = fundaMakelaarCommands.topRealtors(city, filter, topN);

        assertEquals("""
                
                ┌────┬──────────┬────────────┬──────┐
                │Rank│Realtor ID│Realtor Name│Offers│
                ├────┼──────────┼────────────┼──────┤
                │1   │100001    │Hank        │100   │
                │2   │100003    │Jane        │22    │
                └────┴──────────┴────────────┴──────┘
                """, result);
    }
}