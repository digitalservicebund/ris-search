package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Metadata {
  private Identifier identifier;

  private IsVersionOf isVersionOf;

  private Creator creator;

  private Coverage coverage;

  @XmlElement private String date;

  private Language language;

  private Publisher publisher;

  @XmlElement private String accessRights = "public";

  private Type type;
}
