package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.html.service.xslt.CaselawXsltTransformer;
import org.springframework.stereotype.Service;

/** Service for transforming LegalDocML case law documents to HTML using XSLT. */
@Service
public class CaselawXsltTransformerService {

  /**
   * Key for the resource path parameter passed to the XSLT transformer.
   *
   * @param resourcesBasePath
   * @param source
   * @return the transformed HTML as a String
   */
  public String transformCaseLaw(byte[] source, String resourcesBasePath) {
    CaselawXsltTransformer caselawXsltTransformer = new CaselawXsltTransformer();
    return caselawXsltTransformer.transformCaseLaw(source, resourcesBasePath);
  }
}
