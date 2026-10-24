package io.github.oldborn.fundamakelaar2026.service;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.FundaAanbodApiClient;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodObjectDTO;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OfferCollectorService {

    private static final Logger logger = LoggerFactory.getLogger(OfferCollectorService.class);

    private final FundaAanbodApiClient fundaAanbodApiClient;

    private static final int PAGE_SIZE = 100;

    public OfferCollectorService(FundaAanbodApiClient fundaAanbodApiClient) {
        this.fundaAanbodApiClient = fundaAanbodApiClient;
    }
    
    public List<Offer> collectAllOffers(){
        AanbodPageDTO firstPage = fundaAanbodApiClient.fetchPage(1, PAGE_SIZE).block();
        int totalPages = firstPage.paging().aantalPaginas();

        // using Map instead of List to avoid duplicated offers in between pages.
        Map<String, Offer> offers = new HashMap<>(firstPage.totaalAantalObjecten());
        offers.putAll(extractOffersFromPage(firstPage));
        for (int i = 2; i <= totalPages; i++) {
            AanbodPageDTO page = fundaAanbodApiClient.fetchPage(i, PAGE_SIZE).block();
            logger.info("Fetched page: {}/{} with number of offers: {}", i, totalPages, page.objects().size());
            offers.putAll(extractOffersFromPage(page));
        }
        return offers.values().stream().toList();
    }

    protected static Map<String, Offer> extractOffersFromPage(AanbodPageDTO page){
        return page.objects().stream()
                .filter(aanbodObject -> aanbodObject.makelaarId() != null)
                .map(OfferCollectorService::aanBondObjectToOffer)
                .collect(Collectors.toMap(Offer::id, offer -> offer));
    }

    protected static Offer aanBondObjectToOffer(AanbodObjectDTO aanbodObjectDTO){
        return new Offer(
                aanbodObjectDTO.id(),
                aanbodObjectDTO.makelaarId(),
                aanbodObjectDTO.makelaarNaam()
        );
    }
}
