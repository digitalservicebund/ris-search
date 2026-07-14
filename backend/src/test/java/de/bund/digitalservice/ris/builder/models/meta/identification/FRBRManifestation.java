package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.DateLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.FormatLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.HrefLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.ValueLeaf;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Represents the {@code akn:FRBRManifestation} element identifying the norm's manifestation level.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FRBRManifestation extends BaseElement {

  /**
   * Builds an FRBRManifestation from the manifestation-level parts of the given ELI.
   *
   * @param eli the parsed ELI to derive the manifestation identification from
   * @return the built {@link FRBRManifestation}
   */
  public static FRBRManifestation fromEli(EliFile eli) {
    return FRBRManifestation.builder()
        .frbrThis(
            ValueLeaf.builder()
                .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrthis-n1")
                .value(eli.getManifestationEli().toString())
                .build())
        .frbrUri(
            ValueLeaf.builder()
                .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbruri-n1")
                .value(eli.getManifestationEli().getManifestationRoot())
                .build())
        .frbrDate(
            DateLeaf.builder()
                .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrdate-n1")
                .date(eli.pointInTimeManifestation().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .name("generierung")
                .build())
        .frbrFormat(
            FormatLeaf.builder()
                .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrformat-n1")
                .value(eli.format())
                .build())
        .build();
  }

  public void setThis(String manifestationEli) {
    this.frbrThis =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrthis-n1")
            .value(manifestationEli)
            .build();
  }

  @Builder.Default @XmlAttribute private String eId = "meta-n1_ident-n1_frbrmanifestation-n1";

  @Builder.Default
  @XmlElement(name = "FRBRthis", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrThis =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrthis-n1")
          .value(
              "eli/bund/bgbl-1/2025/341/2025-12-22/1/deu/2025-12-22/regelungstext-verkuendung-1.xml")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRuri", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrUri =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbruri-n1")
          .value(
              "eli/bund/bgbl-1/2025/341/2025-12-22/1/deu/2025-12-22/regelungstext-verkuendung-1.xml")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRdate", namespace = NormTestDataBuilder.AKN_NS)
  private DateLeaf frbrDate =
      DateLeaf.builder()
          .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrdate-n1")
          .date("2025-12-22")
          .name("generierung")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRauthor", namespace = NormTestDataBuilder.AKN_NS)
  private HrefLeaf frbrAuthor =
      HrefLeaf.builder()
          .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrauthor-n1")
          .href("recht.bund.de")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRformat", namespace = NormTestDataBuilder.AKN_NS)
  private FormatLeaf frbrFormat =
      FormatLeaf.builder()
          .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrformat-n1")
          .value("xml")
          .build();
}
