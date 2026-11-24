package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;

/** Represents a subject area (Sachgebiet) listed in the proprietary RIS metadata. */
@Getter
public class RisSachgebiet {

  @XmlAttribute(name = "sachgebiet-id")
  private String sachgebieteId;

  @XmlValue private String value;
}
