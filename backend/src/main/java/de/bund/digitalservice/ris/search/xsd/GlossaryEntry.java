package de.bund.digitalservice.ris.search.xsd;

/**
 * A single glossary row: the {@code key} of an XSD-documented element/type and its German/English
 * description text pair. Either description may be blank if no documentation was found for that
 * language.
 */
public record GlossaryEntry(String key, String descriptionDe, String descriptionEn) {}
