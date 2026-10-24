package io.github.oldborn.fundamakelaar2026.service;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.FundaAanbodApiClient;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodObjectDTO;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfferCollectorService {

    private static final Logger logger = LoggerFactory.getLogger(OfferCollectorService.class);

    private final FundaAanbodApiClient fundaAanbodApiClient;

    private static final int PAGE_SIZE = 25;

    public OfferCollectorService(FundaAanbodApiClient fundaAanbodApiClient) {
        this.fundaAanbodApiClient = fundaAanbodApiClient;
    }
    
    public List<Offer> collectAllOffers(){
        // TODO: add unit tests
        AanbodPageDTO firstPage = fundaAanbodApiClient.fetchPage(1, PAGE_SIZE).block();
        int totalPages = firstPage.paging().aantalPaginas();

        List<Offer> offers = new ArrayList<>(firstPage.totaalAantalObjecten());
        offers.addAll(firstPage.objects().stream().map(OfferCollectorService::aanBondObjectToOffer).toList());
        for (int i = 2; i < totalPages - 1; i++) {
            AanbodPageDTO page = fundaAanbodApiClient.fetchPage(i, PAGE_SIZE).block();
            logger.info("Fetched page: {}/{} with number of offers: {}", i, totalPages, page.objects().size());
            offers.addAll(page.objects().stream().map(OfferCollectorService::aanBondObjectToOffer).toList());
        }
        return offers;
    }

    protected static Offer aanBondObjectToOffer(AanbodObjectDTO aanbodObjectDTO){
        return new Offer(
                aanbodObjectDTO.id(),
                aanbodObjectDTO.makelaarId(),
                aanbodObjectDTO.makelaarNaam()
        );
    }
}
