package de.bund.digitalservice.ris.search.models.opensearch;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A model used to represent the OpenSearch {@link Norm} sub-entity "Article", which is any part of
 * that norm. It is named article for historical purposes, but is used to represent non-article
 * parts such as preambles, conclusions, and attachments as well.
 *
 * @param manifestationEli The manifestation ELI of the underlying file, if one exists for this
 *     entity. Generally, the main articles of a {@link Norm} are included in the main file, but
 *     attachments may have their own file.
 */
@Builder
public record Article(
    @Field(name = "name") String name,
    @Field(name = "text") String text,
    @Nullable
        @Field(name = "entry_into_force_date", type = FieldType.Date, format = DateFormat.date)
        LocalDate entryIntoForceDate,
    @Nullable @Field(name = "expiry_date", type = FieldType.Date, format = DateFormat.date)
        LocalDate expiryDate,
    @Nullable @Field(name = "is_active") Boolean isActive,
    @Nullable @Field(name = "eid") String eId,
    @Nullable @Field(name = "guid") String guid,
    @Nullable @Field(name = "manifestation_eli") String manifestationEli) {}
