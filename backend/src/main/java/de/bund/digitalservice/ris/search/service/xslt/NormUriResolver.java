package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** A custom URI resolver for Norm xslt and attachment files */
@Service
public class NormUriResolver implements URIResolver {

  private static final Logger logger = LoggerFactory.getLogger(NormUriResolver.class);
  private final NormsBucket normsBucket;

  public NormUriResolver(NormsBucket normsBucket) {
    this.normsBucket = normsBucket;
  }

  @Override
  public Source resolve(String href, String base) throws TransformerException {
    String uriPath = NormLdmlToOpenSearchMapper.parseURIPathOrThrow(href, "Invalid URI: " + href);
    if ("include/inhalt.xsl".equals(uriPath) || "include/hilfsfunktionen.xsl".equals(uriPath)) {
      // let the default resolver handle requests for included XSL templates
      return null;
    } else if (uriPath.startsWith("eli/")) {
      return resolveEliResource(uriPath);
    } else {
      // Throw an exception on unexpected uris. This prevents arbitrary uri loading.
      throw new TransformerException("Invalid URI path: " + uriPath);
    }
  }

  private StreamSource resolveEliResource(String uriPath) throws TransformerException {
    logger.debug("Resolving attachment: {}", uriPath);
    // validate that the uriPath is a valid manifestation ELI
    EliFile eliFile =
        EliFile.fromString(uriPath)
            .orElseThrow(() -> new TransformerException("Invalid ELI: " + uriPath));

    try {
      var response = this.normsBucket.getStream(eliFile.toString());
      return new StreamSource(response);
    } catch (NoSuchKeyException | NullPointerException e) {
      throw new TransformerException("Failed to resolve: " + eliFile, e);
    }
  }
}
