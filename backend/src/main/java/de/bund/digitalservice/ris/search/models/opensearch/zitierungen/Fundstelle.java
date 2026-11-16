package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Where a referenced document was published (ris:fundstelle). */
@Builder
public record Fundstelle(
    @Field(name = "periodikum") String periodikum,
    @Field(name = "zitatstelle") String zitatstelle,
    @Field(name = "fundstellen_typ") String fundstellenTyp) {}
