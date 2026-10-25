package io.github.oldborn.fundamakelaar2026.service;

import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import io.github.oldborn.fundamakelaar2026.service.model.Realtor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RealtorRankingServiceTest {
    private final RealtorRankingService realtorRankingService = new RealtorRankingService();

    @Test
    public void topRealtors_shouldReturnCorrectResult_fromGivenOfferList(){
        List<Offer> offers = List.of(
                new Offer("1", 101, "Realtor A"),
                new Offer("2", 101, "Realtor A"),
                new Offer("3", 101, "Realtor A"),
                new Offer("4", 102, "Realtor B"),
                new Offer("5", 102, "Realtor B"),
                new Offer("6", 103, "Realtor C"),
                new Offer("7", 103, "Realtor C"),
                new Offer("8", 104, "Realtor D")
        );

        List<Realtor> realtors = realtorRankingService.topRealtors(offers, 3);

        assertEquals(3, realtors.size());

        List<Realtor> expected = List.of(
                new Realtor(101, "Realtor A", 3),
                new Realtor(102, "Realtor B", 2),
                new Realtor(103, "Realtor C", 2)
        );
        assertEquals(expected, realtors);
    }
}
