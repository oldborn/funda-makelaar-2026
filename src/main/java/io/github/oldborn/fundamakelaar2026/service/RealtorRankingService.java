package io.github.oldborn.fundamakelaar2026.service;

import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import io.github.oldborn.fundamakelaar2026.service.model.Realtor;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RealtorRankingService {

    private static final int DEFAULT_TOP_N = 10;

    public List<Realtor> topRealtors(List<Offer> offers, @Nullable Integer topN){
        int topNRank = topN == null ? DEFAULT_TOP_N : topN;
        Map<Integer, List<Offer>> offersByRealtorId = offers.stream().collect(Collectors.groupingBy(Offer::realtorId));
        List<Realtor>  ranking = offersByRealtorId.entrySet().stream()
                .map(entry -> {
                    int realtorId = entry.getKey();
                    List<Offer> realtorOffers = entry.getValue();

                    String realtorName = realtorOffers.getFirst().realtorName();

                    return new Realtor(realtorId, realtorName, realtorOffers.size());
                }).sorted(
                        Comparator.comparingInt(Realtor::numberOfOffers)
                                .reversed()
                                // tiebreaker in case numberOfOffers are equal to make outcome deterministic
                                .thenComparingInt(Realtor::id)
                )
                .limit(topNRank)
                .toList();

        return ranking;
    }
}
