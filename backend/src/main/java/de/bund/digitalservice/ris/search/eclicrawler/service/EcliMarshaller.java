package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemapindex.Sitemapindex;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import org.springframework.stereotype.Service;

@Service
public class EcliMarshaller {

  private final JAXBContext jaxbCtx;

  public EcliMarshaller() throws JAXBException {
    jaxbCtx = JAXBContext.newInstance(Sitemapindex.class, Sitemap.class);
  }

  public String marshallSitemapIndex(Sitemapindex index) throws JAXBException {
    StringWriter sw = new StringWriter();
    Marshaller m = jaxbCtx.createMarshaller();
    m.setProperty(
        Marshaller.JAXB_SCHEMA_LOCATION,
        "http://www.sitemaps.org/schemas/sitemap/0.9 "
            + "http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd");
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal(index, sw);
    return sw.toString();
  }

  public String marshallSitemap(Sitemap sitemap) throws JAXBException {
    StringWriter sw = new StringWriter();
    Marshaller m = jaxbCtx.createMarshaller();
    m.setProperty(
        Marshaller.JAXB_SCHEMA_LOCATION,
        "http://www.sitemaps.org/schemas/sitemap/0.9 "
            + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd "
            + "https://e-justice.europa.eu/eclisearch "
            + "https://e-justice.europa.eu/eclisearch/ecli.xsd");
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal(sitemap, sw);
    return sw.toString();
  }
}
