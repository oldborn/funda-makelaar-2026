package io.github.oldborn.fundamakelaar2026.shell;

import io.github.oldborn.fundamakelaar2026.service.OfferCollectorService;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@CommandGroup(name = "funda-makelaar", description = "commands to test and execute services")
public class FundaMakelaarCommands {
    private static final Logger logger = LoggerFactory.getLogger(FundaMakelaarCommands.class);

    private final OfferCollectorService offerCollectorService;

    public FundaMakelaarCommands(OfferCollectorService offerCollectorService) {
        this.offerCollectorService = offerCollectorService;
    }

    @Command(name = "test-collector", description = "used for testing collector service")
    public String testCollectorService(){
        List<Offer> offers = offerCollectorService.collectAllOffers();
        return "ok";
    }
}
