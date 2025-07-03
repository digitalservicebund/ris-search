package de.bund.digitalservice.ris;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.SchemaOutputResolver;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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

  /**
   * Validates the schema of an xml string to a given JAXBContext
   *
   * @param ctx JAXBContet
   */
  public static void validate(String content, JAXBContext ctx) {
    // Generate XML Schema from the JAXBContext
    final List<ByteArrayOutputStream> schemaDocs = new ArrayList<ByteArrayOutputStream>();
    try {
      ctx.generateSchema(
          new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              StreamResult sr = new StreamResult(baos);
              schemaDocs.add(baos);
              sr.setSystemId(suggestedFileName);
              return sr;
            }
          });

      SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      int size = schemaDocs.size();
      Source[] schemaSources = new Source[size];
      for (int i = 0; i < size; ++i) {
        schemaSources[i] =
            new StreamSource(new ByteArrayInputStream(schemaDocs.get(i).toByteArray()));
      }
      Validator v = sf.newSchema(schemaSources).newValidator();
      v.validate(new StringSource(content));
    } catch (IOException | SAXException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * remove all whitespaces from a given xml string
   *
   * @param original pretty print xml
   * @return String single line xml
   * @throws IOException If an I/O error occurs
   */
  public static String trim(String original) throws IOException {
    BufferedReader br = new BufferedReader(new StringReader(original));
    String line;
    StringBuilder sb = new StringBuilder();
    while ((line = br.readLine()) != null) {
      sb.append(line.trim());
    }
    return sb.toString();
  }
}
