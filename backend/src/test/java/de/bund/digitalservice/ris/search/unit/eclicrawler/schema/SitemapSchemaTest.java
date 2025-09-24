package de.bund.digitalservice.ris.search.unit.eclicrawler.schema;

import static org.testcontainers.shaded.org.apache.commons.io.FileUtils.getFile;

import de.bund.digitalservice.ris.search.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Url;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.SitemapIndexEntry;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.eclicrawler.service.EcliMarshaller;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class SitemapSchemaTest {

  static final EcliMarshaller marshaller;

  static {
    try {
      marshaller = new EcliMarshaller();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

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
    validator.validate(new StreamSource(new StringReader(marshaller.marshallSitemapIndex(index))));
  }

  private EcliCrawlerDocument getTestDocChange(boolean isPublished) {
    return new EcliCrawlerDocument(
        "docNumber",
        "docNumber.xml",
        "ECLI:DE:XX:2025:1111111",
        "BGH",
        "2025-01-01",
        "http://url/to/docNumber",
        isPublished);
  }

  @Test
  void itGeneratesAValidSitemapForActiveDocument() throws SAXException, JAXBException, IOException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    String sitemaPpath =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("schema/sitemap/sitemap.xsd"))
            .getPath();
    String ecliPath =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("schema/sitemap/ecli.xsd"))
            .getPath();

    Schema schema =
        factory.newSchema(
            new Source[] {
              new StreamSource(getFile(ecliPath)), new StreamSource(getFile(sitemaPpath))
            });
    Validator validator = schema.newValidator();

    Sitemap sitemap = new Sitemap();
    Url url = EcliCrawlerDocumentMapper.toSitemapUrl(getTestDocChange(true));
    List<Url> urls = List.of(url);
    sitemap.setUrl(urls);

    String content = marshaller.marshallSitemap(sitemap);

    Assertions.assertEquals(
        "Bundesgerichtshof", url.getDocument().getMetadata().getCreator().getValue());
    validator.validate(new StreamSource(new StringReader(content)));
  }

  @Test
  void itGeneratesAValidSitemapForDeletedDocument()
      throws SAXException, JAXBException, IOException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    String sitemaPpath =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("schema/sitemap/sitemap.xsd"))
            .getPath();
    String ecliPath =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("schema/sitemap/ecli.xsd"))
            .getPath();

    Schema schema =
        factory.newSchema(
            new Source[] {
              new StreamSource(getFile(ecliPath)), new StreamSource(getFile(sitemaPpath))
            });
    Validator validator = schema.newValidator();

    Sitemap sitemap = new Sitemap();
    Url url = EcliCrawlerDocumentMapper.toSitemapUrl(getTestDocChange(false));
    List<Url> urls = List.of(url);
    sitemap.setUrl(urls);

    String content = marshaller.marshallSitemap(sitemap);
    validator.validate(new StreamSource(new StringReader(content)));
  }
}
