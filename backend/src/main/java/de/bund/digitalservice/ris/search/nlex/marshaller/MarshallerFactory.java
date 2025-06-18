package de.bund.digitalservice.ris.search.nlex.marshaller;

import de.bund.digitalservice.ris.search.nlex.result.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

@Service
public class MarshallerFactory {

  private final JAXBContext resultJaxbContext;

  public MarshallerFactory() throws JAXBException {
    this.resultJaxbContext = JAXBContext.newInstance(ObjectFactory.class);
  }

  public Marshaller getResultMarshaller() throws JAXBException {
    Marshaller m = this.resultJaxbContext.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_FRAGMENT, true);

    return m;
  }
}
