package de.bund.digitalservice.ris.search.models.opensearch;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** The court of a referenced decision (ris:gericht). */
@Builder
public record Gericht(
    @Field(name = "gerichtstyp") String gerichtstyp,
    @Field(name = "gerichtsort") String gerichtsort,
    @Field(name = "gerichtsbarkeit") String gerichtsbarkeit,
    @Field(name = "spruchkoerper") String spruchkoerper,
    @Field(name = "sitz_der_aussenstelle") String sitzDerAussenstelle) {}
