package de.bund.digitalservice.ris.search.unit.sitemap.caselaw.schema;

import static org.testcontainers.shaded.org.apache.commons.io.FileUtils.getFile;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.AccessRights;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Coverage;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Creator;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Document;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Identifier;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.IsVersionOf;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Language;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Metadata;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Publisher;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.SupportedLanguages;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Type;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Url;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.EcliMarshaller;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class SitemapSchemaTest {

  @Test
  void itGeneratesAValidSitemapIndex() throws SAXException, JAXBException, IOException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    String path =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("schema/sitemap/siteindex.xsd"))
            .getPath();
    Source schemaFile = new StreamSource(getFile(path));
    Schema schema = factory.newSchema(schemaFile);
    Validator validator = schema.newValidator();

    Sitemapindex index = new Sitemapindex();
    index.setSitemaps(List.of(new SitemapIndexEntry().setLoc("/path/to/sitemap.xml")));
    validator.validate(
        new StreamSource(new StringReader(EcliMarshaller.marshallSitemapIndex(index))));
  }

  @Test
  void itGeneratesAValidSitemap() throws SAXException, JAXBException, IOException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    String path =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("schema/sitemap/sitemap.xsd"))
            .getPath();
    Source schemaFile = new StreamSource(getFile(path));
    Schema schema = factory.newSchema(schemaFile);
    Validator validator = schema.newValidator();

    Sitemap sitemap = new Sitemap();
    List<Url> urls = List.of(new Url().setLoc("path/to/document"));
    sitemap.setUrl(urls);

    validator.validate(new StreamSource(new StringReader(EcliMarshaller.marshallSitemap(sitemap))));
  }

  @Test
  void itGeneratesAValidDocument() throws SAXException, JAXBException, IOException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    String path =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("schema/sitemap/ecli.xsd"))
            .getPath();
    Source schemaFile = new StreamSource(getFile(path));
    Schema schema = factory.newSchema(schemaFile);
    Validator validator = schema.newValidator();

    Document doc = new Document();
    Metadata metadata = new Metadata();
    metadata.setAccessRights(AccessRights.PUBLIC);
    metadata.setIdentifier(
        new Identifier()
            .setValue("http://identifier")
            .setLang(SupportedLanguages.DE)
            .setFormat(Identifier.FORMAT_HTML));
    metadata.setIsVersionOf(
        new IsVersionOf()
            .setValue("ECLI:DE:XX:2025:1111111")
            .setCountry(IsVersionOf.COUNTRY_DE)
            .setCourt("BGH"));
    metadata.setCreator(new Creator().setLang(SupportedLanguages.DE).setValue("creator"));
    metadata.setCoverage(new Coverage().setLang(SupportedLanguages.DE).setValue("coverage"));
    metadata.setDate("2025-01-01");
    metadata.setLanguage(
        new Language()
            .setLanguageType(SupportedLanguages.TYPE_AUTHORITATIVE)
            .setValue(SupportedLanguages.DE));
    metadata.setPublisher(new Publisher().setLang(SupportedLanguages.DE).setValue("publisher"));
    metadata.setType(new Type().setLang(SupportedLanguages.DE).setValue("type"));
    doc.setMetadata(metadata);

    StringReader xmlStringReader = new StringReader(EcliMarshaller.marshallDocument(doc));
    validator.validate(new StreamSource(xmlStringReader));
  }
}
