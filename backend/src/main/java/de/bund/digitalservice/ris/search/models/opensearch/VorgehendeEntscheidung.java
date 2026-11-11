package de.bund.digitalservice.ris.search.models.opensearch;

import java.time.LocalDate;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/** A prior decision of a case law document (ris:vorgehendeEntscheidung). */
@Builder
public record VorgehendeEntscheidung(
    @Field(name = "referenz_uri") String referenzUri,
    @Field(name = "dokumenttyp") String dokumenttyp,
    @Field(name = "entscheidungsdatum", type = FieldType.Date, format = DateFormat.date)
        LocalDate entscheidungsdatum,
    @Field(name = "dokumentnummer") String dokumentnummer,
    @Field(name = "aktenzeichen") String aktenzeichen,
    @Field(name = "gericht") Gericht gericht) {}
