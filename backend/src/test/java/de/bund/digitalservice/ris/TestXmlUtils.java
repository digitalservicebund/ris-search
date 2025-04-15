package de.bund.digitalservice.ris;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class TestXmlUtils {
  private static final TransformerFactory transformerFactory =
      new net.sf.saxon.TransformerFactoryImpl();

  public static String toString(Document doc) {
    try {
      StringWriter writer = new StringWriter();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.transform(new DOMSource(doc), new StreamResult(writer));
      return writer.toString();
    } catch (TransformerException e) {
      throw new FileTransformationException("Error trying to convert XML document to string");
    }
  }
}
