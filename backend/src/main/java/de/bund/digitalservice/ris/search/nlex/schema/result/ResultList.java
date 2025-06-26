package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class ResultList {

  private Navigation navigation;

  @XmlElementWrapper
  @XmlElement(name = "document")
  private List<Document> documents;
}
