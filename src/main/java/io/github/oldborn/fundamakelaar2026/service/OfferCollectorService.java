package io.github.oldborn.fundamakelaar2026.service;

import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.FundaAanbodApiClient;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodObjectDTO;
import io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto.AanbodPageDTO;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OfferCollectorService {

    private static final Logger logger = LoggerFactory.getLogger(OfferCollectorService.class);

    private final FundaAanbodApiClient fundaAanbodApiClient;

    private static final int PAGE_SIZE = 25;
    private static final int MAX_CONCURRENCY = 5;

    public OfferCollectorService(FundaAanbodApiClient fundaAanbodApiClient) {
        this.fundaAanbodApiClient = fundaAanbodApiClient;
    }
    
    public List<Offer> collectAllOffers(String city, @Nullable String filter){
        AtomicInteger collectedPages = new AtomicInteger(0);
        AanbodPageDTO firstPage = fundaAanbodApiClient.fetchPage(1, PAGE_SIZE, city, filter)
                .blockOptional().orElseThrow();

        int totalPages = firstPage.paging().aantalPaginas();

        return Flux.range(2, Math.max(0, totalPages - 1))
                .flatMap(page -> fundaAanbodApiClient.fetchPage(page, PAGE_SIZE, city, filter), MAX_CONCURRENCY)
                .startWith(firstPage)
                .doOnNext(page -> logger.info(
                        "Fetched page: {}/{} with number of offers: {}",
                        collectedPages.incrementAndGet(),
                        totalPages,
                        page.objects().size())
                ).flatMapIterable(AanbodPageDTO::objects)
                .filter(aanbodObjectDTO -> aanbodObjectDTO.makelaarId() != null)
                .map(OfferCollectorService::aanbodObjectToOffer)
                .distinct(Offer::id)
                .collectList()
                .block();
    }

    protected static Offer aanbodObjectToOffer(AanbodObjectDTO aanbodObjectDTO){
        return new Offer(
                aanbodObjectDTO.id(),
                aanbodObjectDTO.makelaarId(),
                aanbodObjectDTO.makelaarNaam()
        );
    }
}
