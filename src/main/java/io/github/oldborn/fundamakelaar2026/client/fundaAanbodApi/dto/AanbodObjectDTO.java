package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AanbodObjectDTO(
        @JsonProperty("Adres")
        String adres,
        @JsonProperty("GlobalId")
        long globalId,
        @JsonProperty("Id")
        String id,
        @JsonProperty("IsSearchable")
        boolean isSearchable,
        @JsonProperty("IsVerhuurd")
        boolean isVerhuurd,
        @JsonProperty("IsVerkocht")
        boolean isVerkocht,
        @JsonProperty("KoopprijsTot")
        Integer koopprijsTot,
        @JsonProperty("MakelaarId")
        Integer makelaarId,
        @JsonProperty("MakelaarNaam")
        String makelaarNaam,
        @JsonProperty("Postcode")
        String postcode,
        @JsonProperty("URL")
        String url,
        @JsonProperty("VerkoopStatus")
        String verkoopStatus,
        @JsonProperty("Woonplaats")
        String woonplaats
) {}
