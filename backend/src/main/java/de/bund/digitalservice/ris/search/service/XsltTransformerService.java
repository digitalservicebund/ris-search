package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.exception.XMLElementNotFoundException;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.jaxp.TransformerImpl;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class XsltTransformerService {

  private static final Logger logger = LogManager.getLogger(XsltTransformerService.class);

  private static final String XSLT_PATH = "XSLT/html/ldml_de/";
  private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
  private final String normXslt = getXslt(XSLT_PATH + "/ris-portal.xsl");
  private final String caseLawXslt = getXslt("XSLT/html/case-law.xslt");

  private final NormsBucket normsBucket;

  public XsltTransformerService(NormsBucket normsBucket) {
    this.normsBucket = normsBucket;
    URIResolver defaultResolver = transformerFactory.getURIResolver();

    /*
     * The URIResolver handles requests for XSLT file includes and other documents requested by
     * `document()` calls.
     */
    URIResolver resolver =
        (href, base) -> {
          if (Objects.equals(href, "include/inhalt.xsl")
              || Objects.equals(href, "include/hilfsfunktionen.xsl")) {
            // let the default resolver handle requests for included XSL templates
            return defaultResolver != null ? defaultResolver.resolve(href, base) : null;
          } else if (href.startsWith("eli/")) {
            logger.debug("Resolving attachment: {}", href);
            // validate that the href is a valid manifestation ELI
            var eli = ManifestationEli.fromString(href);
            return resolveManifestationEliResource(
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
  private StreamSource resolveManifestationEliResource(ManifestationEli eli)
      throws TransformerException {
    try {
      var response = this.normsBucket.getStream(eli.toString());
      return new StreamSource(response);
    } catch (NoSuchKeyException | NullPointerException e) {
      throw new TransformerException("Failed to resolve: " + eli, e);
    }
  }

  public String transformNorm(byte[] source, String basePath, String resourcesBasePath) {
    Map<String, String> parameters =
        Map.of("dokumentpfad", basePath, "debugging", "false", "ressourcenpfad", resourcesBasePath);
    return transformLegalDocMlFromBytes(source, parameters, normXslt);
  }

  public String transformArticle(byte[] source, String eId, String resourcesBasePath) {
    Map<String, String> parameters =
        Map.of("article-eid", eId, "debugging", "false", "ressourcenpfad", resourcesBasePath);
    return transformLegalDocMlFromBytes(source, parameters, normXslt);
  }

  public String transformCaseLaw(byte[] source) {
    return transformLegalDocMlFromBytes(source, new HashMap<>(), caseLawXslt);
  }

  private String transformLegalDocMlFromBytes(
      byte[] source, Map<String, String> parameters, String xslt) {

    AtomicReference<String> terminationMessage = new AtomicReference<>();
    try {
      String url = new ClassPathResource(XSLT_PATH).getURL().toString();
      Source xsltSource = new StreamSource(new StringReader(xslt), url);

      Transformer transformer = transformerFactory.newTransformer(xsltSource);
      ((TransformerImpl) transformer)
          .getUnderlyingXsltTransformer()
          .setMessageHandler(
              message -> {
                if (message.isTerminate()) {
                  terminationMessage.set(message.toString());
                } else {
                  logger.debug(message.getStringValue());
                }
              });
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      parameters.forEach(transformer::setParameter);
      transformer.setParameter("outputMode", "HTML_ALL");
      StringWriter output = new StringWriter();
      transformer.transform(
          new StreamSource(new ByteArrayInputStream(source)), new StreamResult(output));
      return output.toString();
    } catch (TransformerException | IOException e) {
      logger.error("XSLT transformation error.", e);

      if (terminationMessage.get() != null) {
        var split = terminationMessage.get().split(": ");
        if (split.length == 2 && split[0].equals("EID_NOT_FOUND")) {
          throw new XMLElementNotFoundException(terminationMessage.get());
        }
        if (split.length > 0 && split[0].equals("DOCUMENT_REF_NOT_FOUND")) {
          throw new FileTransformationException(terminationMessage.get());
        }
      }

      throw new FileTransformationException(e.getMessage(), e);
    }
  }

  private String getXslt(String fileName) {
    try {
      ClassPathResource xsltResource = new ClassPathResource(fileName);
      return IOUtils.toString(xsltResource.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      logger.error("XSLT transformation error.", e);
      throw new FileTransformationException(e.getMessage());
    }
  }
}
