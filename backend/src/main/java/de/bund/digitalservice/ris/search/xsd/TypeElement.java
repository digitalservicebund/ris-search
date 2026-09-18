package de.bund.digitalservice.ris.search.xsd;

/** An XSD {@code xs:simpleType}/{@code xs:complexType} identified by its namespace and name. */
public record TypeElement(String namespaceUri, String name) implements XSDElement {}
