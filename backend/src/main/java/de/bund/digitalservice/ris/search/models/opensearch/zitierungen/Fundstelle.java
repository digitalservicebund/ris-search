package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * Where a referenced document was published (ris:fundstelle).
 *
 * <p>{@code periodikum} holds the periodical's abbreviation.
 */
@Builder
public record Fundstelle(
    @Field(name = "periodikum") String periodikum,
    @Field(name = "periodikum_titel") String periodikumTitel,
    @Field(name = "zitatstelle") String zitatstelle,
    @Field(name = "fundstellen_typ") String fundstellenTyp) {}
