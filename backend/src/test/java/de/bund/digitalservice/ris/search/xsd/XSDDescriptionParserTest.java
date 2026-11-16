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
    assertThat(parser.getTypeDocumentation())
        .containsExactly(new TypeDocumentation("ris:tenor", "Tenor", "Ruling", "ris.xsd"));
  }

  @Test
  void anElementNameDoesNotGetTheDescriptionOfItsType() throws IOException {
    var schema =
        writeSchema(
            "ris.xsd",
            """
            <xs:complexType name="organisationJahr">
              <xs:annotation>
                <xs:documentation xml:lang="de">Jahr einer Organisation</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            <xs:complexType name="organisation">
              <xs:sequence>
                <xs:element name="jahr" type="ris:organisationJahr" />
              </xs:sequence>
            </xs:complexType>
            """);

    var parser = parse(schema);

    assertThat(parser.findDescription("ris:organisationJahr", "de"))
        .hasValue("Jahr einer Organisation");
    assertThat(parser.findDescription("ris:jahr", "de")).isEmpty();
    assertThat(parser.getTypeDocumentation())
        .extracting(TypeDocumentation::typeName)
        .containsExactly("ris:organisationJahr");
  }

  @Test
  void documentedEnumerationValuesAreListedAfterTheTypeText() throws IOException {
    var schema =
        writeSchema(
            "ris.xsd",
            """
            <xs:simpleType name="referenzRichtungValue">
              <xs:annotation>
                <xs:documentation xml:lang="de">Richtung der Referenz</xs:documentation>
              </xs:annotation>
              <xs:restriction base="xs:string">
                <xs:enumeration value="passiv">
                  <xs:annotation>
                    <xs:documentation>
                      Im in der Referenz angegebenen Dokument
                      wird dieses Dokument zitiert.
                    </xs:documentation>
                  </xs:annotation>
                </xs:enumeration>
                <xs:enumeration value="aktiv">
                  <xs:annotation>
                    <xs:documentation>Dieses Dokument zitiert.</xs:documentation>
                  </xs:annotation>
                </xs:enumeration>
              </xs:restriction>
            </xs:simpleType>
            """);

    var parser = parse(schema);

    assertThat(parser.findDescription("ris:referenzRichtungValue", "de"))
        .hasValue(
            """
            Richtung der Referenz
            * passiv: Im in der Referenz angegebenen Dokument wird dieses Dokument zitiert.
            * aktiv: Dieses Dokument zitiert.\
            """);
  }

  @Test
  void documentationOfAnAnonymousTypeIsIgnored() throws IOException {
    var schema =
        writeSchema(
            "ris.xsd",
            """
            <xs:element name="tenor">
              <xs:complexType>
                <xs:annotation>
                  <xs:documentation xml:lang="de">Anonymer Typ</xs:documentation>
                </xs:annotation>
              </xs:complexType>
            </xs:element>
            """);

    var parser = parse(schema);

    assertThat(parser.getTypeDocumentation()).isEmpty();
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
    assertThat(parser.getTypeDocumentation())
        .extracting(TypeDocumentation::schemaFile)
        .containsExactly("caselaw.xsd");
  }

  @Test
  void typesOfOtherNamespacesAreIgnored() throws IOException {
    var schema =
        writeSchema(
            "schema/ris/ris.xsd",
            """
            <xs:import namespace="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                       schemaLocation="../akn/akomantoso30.xsd" />
            <xs:complexType name="tatbestand">
              <xs:annotation>
                <xs:documentation xml:lang="de">Tatbestand</xs:documentation>
              </xs:annotation>
            </xs:complexType>
            """);
    // Like the real Akoma Ntoso schema, this one uses the xsd: prefix instead of xs:.
    var akn = tempDir.resolve("schema/akn/akomantoso30.xsd");
    Files.createDirectories(akn.getParent());
    Files.writeString(
        akn,
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                    xmlns="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                    targetNamespace="http://docs.oasis-open.org/legaldocml/ns/akn/3.0">
          <xsd:complexType name="maincontent">
            <xsd:annotation>
              <xsd:documentation>Main content</xsd:documentation>
            </xsd:annotation>
          </xsd:complexType>
        </xsd:schema>
        """);

    var parser = parse(schema);

    assertThat(parser.getTypeDocumentation())
        .extracting(TypeDocumentation::typeName)
        .containsExactly("ris:tatbestand");
  }

  @Test
  void aMissingSchemaLeavesTheDescriptionsEmpty() {
    var parser = parse(tempDir.resolve("does-not-exist.xsd"));

    assertThat(parser.getTypeDocumentation()).isEmpty();
  }

  @Test
  void anUnsetSchemaLocationLeavesTheDescriptionsEmpty() {
    var parser =
        new XSDDescriptionParser(new XSDDescriptionProperties(), new DefaultResourceLoader());

    assertThat(parser.getTypeDocumentation()).isEmpty();
  }
}
