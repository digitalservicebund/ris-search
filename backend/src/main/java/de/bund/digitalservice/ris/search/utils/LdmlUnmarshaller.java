package de.bund.digitalservice.ris.search.utils;

import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.UnmarshalException;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;

public class LdmlUnmarshaller {

  private LdmlUnmarshaller() {}

  public static <L> L unmarshall(String ldmlFile, Class<L> clazz) throws UnmarshalException {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new StringReader(ldmlFile));
      return JAXB.unmarshal(ldmlStreamSource, clazz);
    } catch (DataBindingException e) {
      throw new UnmarshalException("Error unmarshalling ldml file", e);
    }
  }
}
