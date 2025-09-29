package de.bund.digitalservice.ris.search.service.xslt;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LiteratureXsltTransformerService extends XsltTransformer {

  @Override
  String getXsltBasePath() {
    return "XSLT/html/";
  }

  @Override
  String getXsltFilename() {
    return "literature.xslt";
  }

  public String transformLiterature(byte[] source, String resourcesBasePath) {
    Map<String, String> parameters = Map.of(RESOURCE_PATH_KEY, resourcesBasePath);
    return transformLegalDocMlFromBytes(source, parameters);
  }
}
