package de.bund.digitalservice.ris.search.schema;

import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.util.List;
import lombok.Builder;

@Builder
@JsonldResource
@JsonldType("SearchResult")
public record SearchMemberSchema<T extends AbstractDocumentSchema>(
    T item, List<TextMatchSchema> textMatches) {}
