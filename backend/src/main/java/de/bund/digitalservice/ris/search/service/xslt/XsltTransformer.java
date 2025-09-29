package de.bund.digitalservice.ris.search.service.xslt;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import de.bund.digitalservice.ris.search.service.exception.XMLElementNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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
import org.springframework.core.io.ClassPathResource;

public abstract class XsltTransformer {
  static final String RESOURCE_PATH_KEY = "ressourcenpfad";

  final Logger logger = LogManager.getLogger(XsltTransformer.class);
  final TransformerFactory transformerFactory = TransformerFactory.newInstance();

  abstract String getXsltBasePath();

  abstract String getXsltFilename();

  String transformLegalDocMlFromBytes(byte[] source, Map<String, String> parameters) {

    AtomicReference<String> terminationMessage = new AtomicReference<>();
    try {
      String url = new ClassPathResource(getXsltBasePath()).getURL().toString();
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
    try {
      ClassPathResource xsltResource = new ClassPathResource(getXsltBasePath() + getXsltFilename());
      return IOUtils.toString(xsltResource.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      logger.error("XSLT transformation error.", e);
      throw new FileTransformationException(e.getMessage(), e);
    }
  }
}
