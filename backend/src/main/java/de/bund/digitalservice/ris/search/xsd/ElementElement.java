package de.bund.digitalservice.ris.search.xsd;

/** An XSD {@code xs:element} identified by its namespace and name. */
public record ElementElement(String namespaceUri, String name) implements XSDElement {}
