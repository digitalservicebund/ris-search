package de.bund.digitalservice.ris.search.schema;

/**
 * A schema that represents the count of statistical results. This record is used to encapsulate the
 * total count of items or events for statistical purposes within the search schema.
 */
public record StatisticsCountSchema(long count) {}
