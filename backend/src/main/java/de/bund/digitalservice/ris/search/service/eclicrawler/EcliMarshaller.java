package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.Sitemapindex;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import org.springframework.stereotype.Service;

/**
 * Service for marshalling Sitemapindex and Sitemap objects into their XML string representations.
 */
@Service
public class EcliMarshaller {

  private final JAXBContext jaxbCtx;

  public EcliMarshaller() throws JAXBException {
    jaxbCtx = JAXBContext.newInstance(Sitemapindex.class, Sitemap.class);
  }

  /**
   * Marshalls a Sitemapindex object into its XML representation as a string.
   *
   * @param index Sitemapindex object to be marshalled
   * @return XML string representation of the Sitemapindex
   * @throws JAXBException if an error occurs during marshalling
   */
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

  /**
   * Converts a Sitemap object into its XML representation as a string. This process involves
   * marshalling using the JAXBContext and applies specific schema location and formatting
   * properties.
   *
   * @param sitemap the Sitemap object to be converted into an XML string
   * @return a string containing the XML representation of the provided Sitemap
   * @throws JAXBException if an error occurs during the marshalling process
   */
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
