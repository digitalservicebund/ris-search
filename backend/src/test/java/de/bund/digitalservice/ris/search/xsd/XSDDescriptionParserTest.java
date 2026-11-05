package de.bund.digitalservice.ris.search.xsd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;

class XSDDescriptionParserTest {

  private static final String SCHEMA_START =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 xmlns:ris="http://rechtsinformationen.bund.de/schema/ris/0.1"
                 targetNamespace="http://rechtsinformationen.bund.de/schema/ris/0.1">
      """;

  @TempDir private Path tempDir;

  private Path writeSchema(String fileName, String content) throws IOException {
    var file = tempDir.resolve(fileName);
    Files.createDirectories(file.getParent());
    Files.writeString(file, SCHEMA_START + content + "</xs:schema>");
    return file;
  }

  private static XSDDescriptionParser parse(Path schema) {
    var properties = new XSDDescriptionProperties();
    properties.setRisNamespaceLocation(schema.toUri().toString());
    return new XSDDescriptionParser(properties, new DefaultResourceLoader());
  }

  @Test
  void readsTheGermanAndEnglishDescriptionOfAType() throws IOException {
    var schema =
        writeSchema(
            "ris.xsd",
            """
            <xs:complexType name="tenor">
              <xs:annotation>
                <xs:documentation xml:lang="de">Tenor</xs:documentation>
                <xs:documentation xml:lang="en">Ruling</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            """);

    var parser = parse(schema);

    assertThat(parser.findDescription("ris:tenor", "de")).hasValue("Tenor");
    assertThat(parser.findDescription("ris:tenor", "en")).hasValue("Ruling");
    assertThat(parser.getGlossaryEntries())
        .containsExactly(new GlossaryEntry("ris:tenor", "Tenor", "Ruling"));
  }

  @Test
  void anElementGetsTheDescriptionOfItsType() throws IOException {
    var schema =
        writeSchema(
            "ris.xsd",
            """
            <xs:complexType name="aktenzeichenType">
              <xs:annotation>
                <xs:documentation xml:lang="de">Aktenzeichen</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            <xs:element name="aktenzeichen" type="ris:aktenzeichenType" />
            """);

    var parser = parse(schema);

    assertThat(parser.findDescription("ris:aktenzeichen", "de")).hasValue("Aktenzeichen");
  }

  @Test
  void aTypesOwnDescriptionWinsOverAnElementOfTheSameName() throws IOException {
    var schema =
        writeSchema(
            "ris.xsd",
            """
            <xs:complexType name="titel">
              <xs:annotation>
                <xs:documentation xml:lang="de">Titel</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            <xs:complexType name="personTitel">
              <xs:annotation>
                <xs:documentation xml:lang="de">Titel einer Person</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            <xs:complexType name="person">
              <xs:sequence>
                <xs:element name="titel" type="ris:personTitel" />
              </xs:sequence>
            </xs:complexType>
            """);

    var parser = parse(schema);

    assertThat(parser.findDescription("ris:titel", "de")).hasValue("Titel");
    assertThat(parser.findDescription("ris:personTitel", "de")).hasValue("Titel einer Person");
  }

  @Test
  void anElementNameBoundToTypesWithDifferentDescriptionsIsLeftOut() throws IOException {
    var schema =
        writeSchema(
            "ris.xsd",
            """
            <xs:complexType name="organisationJahr">
              <xs:annotation>
                <xs:documentation xml:lang="de">Jahr einer Organisation</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            <xs:complexType name="einzelnormJahr">
              <xs:annotation>
                <xs:documentation xml:lang="de">Jahr</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            <xs:complexType name="organisation">
              <xs:sequence>
                <xs:element name="jahr" type="ris:organisationJahr" />
              </xs:sequence>
            </xs:complexType>
            <xs:complexType name="einzelnorm">
              <xs:sequence>
                <xs:element name="jahr" type="ris:einzelnormJahr" />
              </xs:sequence>
            </xs:complexType>
            """);

    var parser = parse(schema);

    assertThat(parser.findDescription("ris:jahr", "de")).isEmpty();
    assertThat(parser.getGlossaryEntries())
        .extracting(GlossaryEntry::key)
        .containsExactly("ris:einzelnormJahr", "ris:organisationJahr");
  }

  @Test
  void followsIncludesRelativeToTheIncludingFile() throws IOException {
    var schema = writeSchema("schema/ris/ris.xsd", "<xs:include schemaLocation=\"caselaw.xsd\" />");
    writeSchema(
        "schema/ris/caselaw.xsd",
        """
        <xs:complexType name="tatbestand">
          <xs:annotation>
            <xs:documentation xml:lang="de">Tatbestand</xs:documentation>
          </xs:annotation>
        </xs:complexType>
        """);
    // A file with the same name one directory up must not be picked up instead.
    writeSchema(
        "schema/caselaw.xsd",
        """
        <xs:complexType name="tatbestand">
          <xs:annotation>
            <xs:documentation xml:lang="de">Falsche Datei</xs:documentation>
          </xs:annotation>
        </xs:complexType>
        """);

    var parser = parse(schema);

    assertThat(parser.findDescription("ris:tatbestand", "de")).hasValue("Tatbestand");
  }

  @Test
  void aMissingSchemaLeavesTheDescriptionsEmpty() {
    var parser = parse(tempDir.resolve("does-not-exist.xsd"));

    assertThat(parser.getDescriptions()).isEmpty();
    assertThat(parser.getGlossaryEntries()).isEmpty();
  }

  @Test
  void anUnsetSchemaLocationLeavesTheDescriptionsEmpty() {
    var parser =
        new XSDDescriptionParser(new XSDDescriptionProperties(), new DefaultResourceLoader());

    assertThat(parser.getDescriptions()).isEmpty();
    assertThat(parser.getGlossaryEntries()).isEmpty();
  }
}
