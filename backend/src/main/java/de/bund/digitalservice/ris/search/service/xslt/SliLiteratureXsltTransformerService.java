package de.bund.digitalservice.ris.search.service.xslt;

import java.util.Collections;
import org.springframework.stereotype.Service;

/** Service for transforming LegalDocML literature documents into HTML using XSLT. */
@Service
public class SliLiteratureXsltTransformerService extends XsltTransformer {

  @Override
  String getXsltBasePath() {
    return "XSLT/html/";
  }

  @Override
  String getXsltFilename() {
    return "sli-literature.xslt";
  }

  public String transformLiterature(byte[] source) {
    return transformLegalDocMlFromBytes(source, Collections.emptyMap());
  }
}
