package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/** Service for transforming LegalDocML norm and article documents to HTML using XSLT. */
@Service
public class NormXsltTransformerService extends XsltTransformer {

  private final NormsBucket normsBucket;

  @Override
  String getXsltBasePath() {
    return "XSLT/html/ldml_de/";
  }

  @Override
  String getXsltFilename() {
    return "ris-portal.xsl";
  }

  /**
   * Constructs a new instance of {@code NormXsltTransformerService}.
   *
   * @param normsBucket The object storage bucket for norms files.
   */
  public NormXsltTransformerService(NormsBucket normsBucket) {
    this.normsBucket = normsBucket;
    setCustomUriResolver();
  }

  /*
   * The URIResolver handles requests for XSLT file includes and other documents requested by
   * `document()` calls.
   */
  private void setCustomUriResolver() {
    URIResolver defaultResolver = transformerFactory.getURIResolver();

    URIResolver resolver =
        (href, base) -> {
          if (Objects.equals(href, "include/inhalt.xsl")
              || Objects.equals(href, "include/hilfsfunktionen.xsl")) {
            // let the default resolver handle requests for included XSL templates
            return defaultResolver != null ? defaultResolver.resolve(href, base) : null;
          } else if (href.startsWith("eli/")) {
            logger.debug("Resolving attachment: {}", href);
            // validate that the href is a valid manifestation ELI
            Optional<EliFile> eli = EliFile.fromString(href);
            return resolveEliResource(
                eli.orElseThrow(() -> new TransformerException("Invalid ELI: " + href)));
          } else {
            throw new TransformerException("Invalid URI: " + href);
          }
        };
    transformerFactory.setURIResolver(resolver);
  }

  /**
   * Loads a manifestation ELI from the normsBucket.
   *
   * @throws TransformerException The following exceptions should be caught by the XSLT to provide
   *     more specific indications as to where the error occurred.
   */
  @NotNull
  private StreamSource resolveEliResource(EliFile eliFile) throws TransformerException {
    try {
      var response = this.normsBucket.getStream(eliFile.toString());
      return new StreamSource(response);
    } catch (NoSuchKeyException | NullPointerException e) {
      throw new TransformerException("Failed to resolve: " + eliFile, e);
    }
  }

  /**
   * Transforms a LegalDocML norm document.
   *
   * @param source the xml file that will be transformed
   * @param language This is the language from {@link
   *     de.bund.digitalservice.ris.search.utils.eli.ManifestationEli#language()}.
   * @param resourcesBasePath the base path of the resources. For example /v1/legislation/
   * @param subtype This is the subtype from {@link
   *     de.bund.digitalservice.ris.search.utils.eli.ManifestationEli#subtype()}.
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
