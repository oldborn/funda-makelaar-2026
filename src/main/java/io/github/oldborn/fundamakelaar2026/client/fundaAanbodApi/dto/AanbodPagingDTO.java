package io.github.oldborn.fundamakelaar2026.client.fundaAanbodApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AanbodPagingDTO(
        @JsonProperty("AantalPaginas")
        int aantalPaginas,
        @JsonProperty("HuidigePagina")
        int huidigePagina,
        @JsonProperty("VolgendeUrl")
        String volgendeUrl,
        @JsonProperty("VorigeUrl")
        String vorigeUrl
) {}
