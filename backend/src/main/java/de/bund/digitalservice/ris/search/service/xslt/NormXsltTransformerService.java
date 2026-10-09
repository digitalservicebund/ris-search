package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.search.utils.eli.ManifestationEliPath;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Service for transforming LegalDocML norm and article documents to HTML using XSLT. */
@Service
public class NormXsltTransformerService extends XsltTransformer {

  /**
   * Constructs a new instance of {@code NormXsltTransformerService}.
   *
   * @param normUriResolver a custom uri resolver for transforming norms
   */
  public NormXsltTransformerService(NormUriResolver normUriResolver) {
    super("XSLT/html/ldml_de/", "ris-portal.xsl");
    this.transformerFactory.setURIResolver(normUriResolver);
  }

  /**
   * Transforms a LegalDocML norm document.
   *
   * @param source the xml file that will be transformed
   * @param language This is the language from {@link ManifestationEliPath#language()}.
   * @param resourcesBasePath the base path of the resources. For example /v1/legislation/
   * @param subtype This is the subtype from {@link ManifestationEliPath#subtype()}.
   * @return the transformed norm as HTML string
   */
  public String transformNorm(
      byte[] source, String language, String resourcesBasePath, String subtype) {
    var parameters = standardParameters(resourcesBasePath);
    parameters.put("dokumentpfad", language);
    parameters.put("subtype", subtype);
    return transformLegalDocMlFromBytes(source, parameters);
  }

  /**
   * Transforms a LegalDocML article document. It is not guaranteed that an eId is encoded or not.
   * In case an identifier is not found a retry with a UTF-8 encoded identifier will be performed
   *
   * @param source the xml file that will be transformed
   * @param eId the eid of the current article that is getting transformed
   * @param resourcesBasePath the base path of the resources. For example /v1/legislation/
   * @return the transformed article as HTML string
   */
  public String transformArticle(byte[] source, String eId, String resourcesBasePath) {
    var parameters = standardParameters(resourcesBasePath);
    parameters.put("article-eid", eId);
    return transformLegalDocMlFromBytes(source, parameters);
  }

  private Map<String, String> standardParameters(String resourcesBasePath) {
    return new HashMap<>(
        Map.ofEntries(
            Map.entry("debugging", "false"), Map.entry("ressourcenpfad", resourcesBasePath)));
  }
}
