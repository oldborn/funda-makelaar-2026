package io.github.oldborn.fundamakelaar2026.shell;

import io.github.oldborn.fundamakelaar2026.service.OfferCollectorService;
import io.github.oldborn.fundamakelaar2026.service.RealtorRankingService;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import io.github.oldborn.fundamakelaar2026.service.model.Realtor;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@CommandGroup(name = "funda-makelaar", description = "commands to test and execute services")
public class FundaMakelaarCommands {
    private static final Logger logger = LoggerFactory.getLogger(FundaMakelaarCommands.class);

    private final OfferCollectorService offerCollectorService;
    private final RealtorRankingService realtorRankingService;

    public FundaMakelaarCommands(OfferCollectorService offerCollectorService, RealtorRankingService realtorRankingService) {
        this.offerCollectorService = offerCollectorService;
        this.realtorRankingService = realtorRankingService;
    }

    @Command(name = "test-collector", description = "used for testing collector service")
    public String testCollectorService(
            @Option(shortName = 'c', longName = "city", description = "city that is going to be searched", defaultValue = "amsterdam")
            String city,
            @Option(shortName = 'f', longName = "filter", description = "filter that is going to be applied to the search")
            @Nullable String filter
    ){
        List<Offer> offers = offerCollectorService.collectAllOffers(city, filter);
        List<Realtor> realtors = realtorRankingService.topRealtors(offers, 10);
        return realtors.stream()
                .map(realtor -> String.format("%s\t%s\t%d", realtor.id(), realtor.name(), realtor.numberOfOffers())).collect(Collectors.joining("\n"));
    }
}
