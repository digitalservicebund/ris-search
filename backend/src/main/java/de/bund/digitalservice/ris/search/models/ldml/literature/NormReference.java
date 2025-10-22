package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import java.util.Date;
import lombok.Getter;

@Getter
public class NormReference {

  @XmlAttribute(name = "abbreviation")
  private String abbreviation;

  @XmlAttribute(name = "dateOfVersion")
  private Date dateOfVersion;

  @XmlAttribute(name = "singleNorm")
  private String singleNorm;
}
