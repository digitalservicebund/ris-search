package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
@XmlRootElement(namespace = "https://e-justice.europa.eu/ecli")
@XmlType(propOrder = {"status", "metadata"})
public class Document {
  public static final String STATUS_DELETED = "deleted";
  private Metadata metadata;

  private String status;
}
