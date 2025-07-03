package de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Document {
  public static final String STATUS_DELETED = "deleted";
  private Metadata metadata;

  private String status;
}
