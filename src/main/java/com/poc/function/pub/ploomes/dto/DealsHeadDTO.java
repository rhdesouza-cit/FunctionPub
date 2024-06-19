package com.poc.function.pub.ploomes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DealsHeadDTO(
    @JsonProperty("@odata.context")
    String context,
    List<DealsDTO> value
) {}
