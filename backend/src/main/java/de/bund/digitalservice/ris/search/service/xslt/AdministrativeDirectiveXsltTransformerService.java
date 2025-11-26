package de.bund.digitalservice.ris.search.service.xslt;

import java.util.Collections;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for transforming LegalDocML source documents to HTML output using the
 * "administrative-directive.xslt" XSLT file.
 *
 * <p>This class extends the {@link XsltTransformer} abstract base class and provides implementation
 * details specific to the transformation of LegalDocML documents related to administrative
 * directives.
 *
 * <p>The transformation process applies an XSLT transformation using predefined configurations for
 * the base path and XSLT file specific to administrative directives. Transformation leverages
 * parameters for customization and supports an HTML output mode.
 */
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
