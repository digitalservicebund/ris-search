package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Content {

  public static final String LANG_DE_DE = "de-DE";

  @XmlAttribute private String lang;

  @XmlElement private String title;

  @XmlElement(name = "para")
  private List<Para> paraList;
}
