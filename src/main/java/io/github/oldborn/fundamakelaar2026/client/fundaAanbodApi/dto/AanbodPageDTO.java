package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AanbodPageDTO(
        @JsonProperty("Objects")
        List<AanbodObjectDTO> objects,
        @JsonProperty("Paging")
        AanbodPagingDTO paging,
        @JsonProperty("TotaalAantalObjecten")
        int totaalAantalObjecten
) {}
