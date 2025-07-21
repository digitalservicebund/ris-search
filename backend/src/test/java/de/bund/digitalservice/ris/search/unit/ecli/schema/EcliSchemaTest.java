package de.bund.digitalservice.ris.search.unit.ecli.schema;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Document;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Identifier;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Metadata;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Url;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EcliSchemaTest {

  @Test()
  void marshallTest() throws JAXBException {
    Document doc =
        new Document()
            .setMetadata(
                new Metadata()
                    .setIdentifier(
                        new Identifier().setFormat("html").setLang("de").setValue("idnetifier")));
    Sitemap set =
        new Sitemap().setUrl(List.of(new Url().setDocument(doc).setLoc("path_to_caselaw")));
    var sw = new StringWriter();
    JAXBContext ctx = JAXBContext.newInstance(Sitemap.class);
    Marshaller m = ctx.createMarshaller();
    m.setProperty(
        Marshaller.JAXB_SCHEMA_LOCATION,
        "http://www.sitemaps.org/schemas/sitemap/0.9 "
            + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd "
            + "https://e-justice.europa.eu/eclisearch "
            + "https://e-justice.europa.eu/eclisearch/ecli.xsd");
    m.marshal(set, sw);

    String result = sw.toString();
    Assertions.assertFalse(result.isEmpty());
  }
}
