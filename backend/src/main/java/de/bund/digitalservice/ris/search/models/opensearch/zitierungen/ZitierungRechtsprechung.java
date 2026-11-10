package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import de.bund.digitalservice.ris.search.models.Richtung;
import java.time.LocalDate;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/** A reference to a case law document (ris:referenzRechtsprechung). */
@Builder
public record ZitierungRechtsprechung(
    @Field(name = "richtung") Richtung richtung,
    @Field(name = "referenz_uri") String referenzUri,
    @Field(name = "dokumentnummer") String dokumentnummer,
    @Field(name = "art_der_zitierung") String artDerZitierung,
    @Field(name = "gericht") Gericht gericht,
    @Field(name = "entscheidungsdatum", type = FieldType.Date, format = DateFormat.date)
        LocalDate entscheidungsdatum,
    @Field(name = "dokumenttyp") String dokumenttyp,
    @Field(name = "aktenzeichen") String aktenzeichen,
    @Field(name = "fundstelle") Fundstelle fundstelle) {}
