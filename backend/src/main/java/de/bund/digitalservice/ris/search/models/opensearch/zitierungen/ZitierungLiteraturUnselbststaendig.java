package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** A reference to a dependent literature document (ris:referenzUnselbststaendigeLiteratur). */
@Builder
public record ZitierungLiteraturUnselbststaendig(
    @Field(name = "richtung") Richtung richtung,
    @Field(name = "referenz_uri") String referenzUri,
    @Field(name = "dokumentnummer") String dokumentnummer,
    @Field(name = "autor") String autor,
    @Field(name = "dokumenttyp") String dokumenttyp,
    @Field(name = "fundstelle") Fundstelle fundstelle) {}
