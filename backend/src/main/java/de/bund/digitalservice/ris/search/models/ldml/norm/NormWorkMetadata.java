package de.bund.digitalservice.ris.search.models.ldml.norm;

import de.bund.digitalservice.ris.search.mapper.NormWorkMetadataMapper;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the {@code metadaten} document root of a norm work's {@code metadaten.xml} file. */
@Getter
@XmlRootElement(name = "metadaten", namespace = NormWorkMetadataMapper.NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class NormWorkMetadata {

  @XmlElement(name = "vollzitat", namespace = NormWorkMetadataMapper.NAMESPACE)
  private String vollzitat;

  @XmlElement(name = "standangabe", namespace = NormWorkMetadataMapper.NAMESPACE)
  private List<Standangabe> standangabe = new ArrayList<>();

  @XmlElement(
      name = "standDerDokumentarischenBearbeitung",
      namespace = NormWorkMetadataMapper.NAMESPACE)
  private List<StandDerDokumentarischenBearbeitung> standDerDokumentarischenBearbeitung =
      new ArrayList<>();
}
