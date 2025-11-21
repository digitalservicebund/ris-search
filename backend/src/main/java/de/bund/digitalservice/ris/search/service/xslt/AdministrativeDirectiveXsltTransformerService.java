package de.bund.digitalservice.ris.search.service.xslt;

import java.util.Collections;
import org.springframework.stereotype.Service;

@Service
public class AdministrativeDirectiveXsltTransformerService extends XsltTransformer {
  @Override
  String getXsltBasePath() {
    return "XSLT/html/";
  }

  @Override
  String getXsltFilename() {
    return "administrative-directive.xslt";
  }

  public String transform(byte[] source) {
    return transformLegalDocMlFromBytes(source, Collections.emptyMap());
  }
}
