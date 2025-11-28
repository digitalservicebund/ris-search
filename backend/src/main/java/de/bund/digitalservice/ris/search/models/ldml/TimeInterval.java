package de.bund.digitalservice.ris.search.models.ldml;

/**
 * Represents a time interval with a start and end time. Instances of this class are immutable.
 *
 * <p>The start and end times are expressed as strings and are assumed to follow a consistent time
 * format as defined by the application using this class.
 */
public record TimeInterval(String start, String end) {}
