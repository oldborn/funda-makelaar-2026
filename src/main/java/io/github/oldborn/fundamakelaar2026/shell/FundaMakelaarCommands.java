package io.github.oldborn.fundamakelaar2026.shell;

import io.github.oldborn.fundamakelaar2026.service.OfferCollectorService;
import io.github.oldborn.fundamakelaar2026.service.RealtorRankingService;
import io.github.oldborn.fundamakelaar2026.service.model.Offer;
import io.github.oldborn.fundamakelaar2026.service.model.Realtor;
import jakarta.annotation.Nullable;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.jline.tui.table.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@CommandGroup(name = "funda-makelaar", description = "commands to execute realtor ranking")
public class FundaMakelaarCommands {

    private final OfferCollectorService offerCollectorService;
    private final RealtorRankingService realtorRankingService;

    public FundaMakelaarCommands(OfferCollectorService offerCollectorService, RealtorRankingService realtorRankingService) {
        this.offerCollectorService = offerCollectorService;
        this.realtorRankingService = realtorRankingService;
    }

    @Command(name = "top-realtors", description = "used for testing collector service")
    public String topRealtors(
            @Option(shortName = 'c', longName = "city", description = "city that is going to be searched", defaultValue = "amsterdam")
            String city,
            @Option(shortName = 'f', longName = "filter", description = "filter that is going to be applied to the search")
            @Nullable String filter,
            @Option(shortName = 'n', longName = "topN", description = "top N of the realtors", defaultValue = "10")
            Integer topN
    ){
        List<Offer> offers = offerCollectorService.collectAllOffers(city, filter);
        List<Realtor> realtors = realtorRankingService.topRealtors(offers, topN);
        return renderTable(realtors);
    }

    private String renderTable(List<Realtor> realtors) {
        TableModelBuilder<Object> modelBuilder = new TableModelBuilder<>();

        // headers
        modelBuilder.addRow().addValue("Rank").addValue("Realtor ID").addValue("Realtor Name").addValue("Offers");

        for (int i = 0; i < realtors.size(); i++) {
            Realtor r = realtors.get(i);
            modelBuilder.addRow()
                    .addValue(i + 1)
                    .addValue(r.id())
                    .addValue(r.name())
                    .addValue(r.numberOfOffers());
        }

        return "\n" + new TableBuilder(modelBuilder.build())
                .addHeaderAndVerticalsBorders(BorderStyle.fancy_light)
                .build()
                .render(90);
    }
}
