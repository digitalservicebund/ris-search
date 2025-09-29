package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

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

  public String transformNorm(byte[] source, String basePath, String resourcesBasePath) {
    Map<String, String> parameters =
        Map.of(
            "dokumentpfad", basePath, "debugging", "false", RESOURCE_PATH_KEY, resourcesBasePath);
    return transformLegalDocMlFromBytes(source, parameters);
  }

  public String transformArticle(byte[] source, String eId, String resourcesBasePath) {
    Map<String, String> parameters =
        Map.of("article-eid", eId, "debugging", "false", RESOURCE_PATH_KEY, resourcesBasePath);
    return transformLegalDocMlFromBytes(source, parameters);
  }
}
