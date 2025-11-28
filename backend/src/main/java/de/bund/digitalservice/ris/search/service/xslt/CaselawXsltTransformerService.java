package de.bund.digitalservice.ris.search.service.xslt;

import java.util.Map;
import org.springframework.stereotype.Service;

/** Service for transforming LegalDocML case law documents to HTML using XSLT. */
@Service
public class CaselawXsltTransformerService extends XsltTransformer {

  @Override
  String getXsltBasePath() {
    return "XSLT/html/";
  }

  @Override
  String getXsltFilename() {
    return "case-law.xslt";
  }

  /**
   * Key for the resource path parameter passed to the XSLT transformer.
   *
   * @param resourcesBasePath
   * @param source
   * @return the transformed HTML as a String
   */
  public String transformCaseLaw(byte[] source, String resourcesBasePath) {
    Map<String, String> parameters = Map.of(RESOURCE_PATH_KEY, resourcesBasePath);
    return transformLegalDocMlFromBytes(source, parameters);
  }
}
