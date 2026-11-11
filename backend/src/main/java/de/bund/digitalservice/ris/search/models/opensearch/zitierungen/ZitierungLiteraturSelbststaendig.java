package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** A reference to an independent literature document (ris:referenzSelbststaendigeLiteratur). */
@Builder
public record ZitierungLiteraturSelbststaendig(
    @Field(name = "richtung") Richtung richtung,
    @Field(name = "referenz_uri") String referenzUri,
    @Field(name = "dokumentnummer") String dokumentnummer,
    @Field(name = "autor") String autor,
    @Field(name = "titel") String titel,
    @Field(name = "veroeffentlichungsjahr") String veroeffentlichungsjahr) {}
