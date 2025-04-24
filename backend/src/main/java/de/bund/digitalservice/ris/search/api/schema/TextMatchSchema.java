package de.bund.digitalservice.ris.search.api.schema;

import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import javax.annotation.Nullable;
import lombok.Builder;

@Builder
@JsonldResource
@JsonldType("SearchResultMatch")
public record TextMatchSchema(String name, String text, @Nullable String location) {}
