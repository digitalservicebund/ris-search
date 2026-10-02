package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.html.exception.FileTransformationException;
import de.bund.digitalservice.ris.html.exception.XMLElementNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.jaxp.TransformerImpl;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for XSLT transformers for LegalDocML documents. This is a copy of
 * XsltTransformer in ris-html-transformation. There are small changes until norm html generation is
 * also migrated to ris-html-transformation. Until then, we will try to keep the classes mostly
 * aligned.
 */
public abstract class XsltTransformer {
  static final String RESOURCE_PATH_KEY = "ressourcenpfad";

  private final Logger logger = LogManager.getLogger(XsltTransformer.class);
  protected final TransformerFactory transformerFactory = TransformerFactory.newInstance();

  private final String xsltBasePath;
  private final String xsltFilename;

  /**
   * Used by child classes to construct instances of children XsltTransformer.
   *
   * @param xsltBasePath base path for the xslt files.
   * @param xsltFilename the root xslt file to be applied.
   */
  protected XsltTransformer(String xsltBasePath, String xsltFilename) {
    this.xsltBasePath = xsltBasePath;
    this.xsltFilename = xsltFilename;
  }

  String transformLegalDocMlFromBytes(byte[] source, Map<String, String> parameters) {

    AtomicReference<String> terminationMessage = new AtomicReference<>();
    try {
      // PURE JAVA ALTERNATIVE FOR BASE PATH URL:
      URL basePathUrl = getClass().getClassLoader().getResource(xsltBasePath);
      if (basePathUrl == null) {
        throw new FileTransformationException("XSLT Base path not found: " + xsltBasePath);
      }
      String url = basePathUrl.toString();

      Source xsltSource = new StreamSource(new StringReader(getXslt()), url);

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
      try (ByteArrayInputStream in = new ByteArrayInputStream(source);
          StringWriter output = new StringWriter()) {

        transformer.transform(new StreamSource(in), new StreamResult(output));
        return output.toString();
      }
    } catch (TransformerException | IOException e) {
      logger.error("XSLT transformation error.", e);

      if (terminationMessage.get() != null) {
        var split = terminationMessage.get().split(": ");
        if (split.length == 2 && split[0].equals("EID_NOT_FOUND")) {
          throw new XMLElementNotFoundException(terminationMessage.get(), e);
        }
        if (split.length > 0 && split[0].equals("DOCUMENT_REF_NOT_FOUND")) {
          throw new FileTransformationException(terminationMessage.get(), e);
        }
      }

      throw new FileTransformationException(e.getMessage(), e);
    }
  }

  String getXslt() {
    // PURE JAVA ALTERNATIVE FOR READING STREAM:
    String fullPath = xsltBasePath + xsltFilename;
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fullPath)) {
      if (inputStream == null) {
        throw new FileTransformationException("XSLT file not found: " + fullPath);
      }
      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      logger.error("XSLT transformation error.", e);
      throw new FileTransformationException(e.getMessage(), e);
    }
  }
}
