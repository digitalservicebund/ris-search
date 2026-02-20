package de.bund.digitalservice.ris.search.models.opensearch;

import org.springframework.data.elasticsearch.annotations.Field;

public record FootNote(@Field(name = "id") String id, @Field(name = "text") String text) {}
