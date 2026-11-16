package de.bund.digitalservice.ris.search.models.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.common.Gericht;
import java.time.LocalDate;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/** A subsequent decision of a case law document (ris:nachgehendeEntscheidung). */
@Builder
public record NachgehendeEntscheidung(
    @Field(name = "art") String art,
    @Field(name = "referenz_uri") String referenzUri,
    @Field(name = "dokumenttyp") String dokumenttyp,
    @Field(name = "entscheidungsdatum", type = FieldType.Date, format = DateFormat.date)
        LocalDate entscheidungsdatum,
    @Field(name = "mitteilungsdatum", type = FieldType.Date, format = DateFormat.date)
        LocalDate mitteilungsdatum,
    @Field(name = "dokumentnummer") String dokumentnummer,
    @Field(name = "aktenzeichen") String aktenzeichen,
    @Field(name = "gericht") Gericht gericht,
    @Field(name = "vermerk") String vermerk) {}
