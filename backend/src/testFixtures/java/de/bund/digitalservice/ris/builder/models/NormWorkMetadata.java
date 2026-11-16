package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.search.mapper.NormWorkMetadataMapper;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** Represents the {@code metadaten} document root of a norm work's {@code metadaten.xml} file. */
@XmlRootElement(name = "metadaten", namespace = NormWorkMetadataMapper.NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class NormWorkMetadata {

  @XmlElement(name = "vollzitat", namespace = NormWorkMetadataMapper.NAMESPACE)
  private String vollzitat;
}
