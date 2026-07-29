package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.DateLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.FormatLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.HrefLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.ValueLeaf;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents the {@code akn:FRBRManifestation} element identifying the norm's manifestation level.
 */
public class FRBRManifestation extends BaseElement {

  @XmlAttribute private final String eId;

  @XmlElement(name = "FRBRthis", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrThis;

  @XmlElement(name = "FRBRuri", namespace = NormTestDataBuilder.AKN_NS)
  private final ValueLeaf frbrUri;

  @XmlElement(name = "FRBRdate", namespace = NormTestDataBuilder.AKN_NS)
  private final DateLeaf frbrDate;

  @XmlElement(name = "FRBRauthor", namespace = NormTestDataBuilder.AKN_NS)
  private final HrefLeaf frbrAuthor;

  @XmlElement(name = "FRBRformat", namespace = NormTestDataBuilder.AKN_NS)
  private final FormatLeaf frbrFormat;

  /** FRBRManifestation empty constructor. Calls another constructor with default values. */
  public FRBRManifestation() {
    // eli/bund/bgbl-1/2025/341/2025-12-22/1/deu/2025-12-22/regelungstext-verkuendung-1.xml
    this(
        new ManifestationEli(
            "bund",
            "bgbl-1",
            "2025",
            "341",
            LocalDate.of(2025, 12, 22),
            1,
            "deu",
            LocalDate.of(2025, 12, 22),
            "regelungstext-verkuendung-1",
            "xml"));
  }

  /**
   * FRBRManifestation constructor. Builds a FRBRManifestation from a ManifestationEli.
   *
   * @param eli the manifestation eli for the FRBRManifestation
   */
  public FRBRManifestation(ManifestationEli eli) {

    this.eId = "meta-n1_ident-n1_frbrmanifestation-n1";

    this.frbrThis =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrthis-n1")
            .value(eli.toString())
            .build();

    this.frbrUri =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbruri-n1")
            .value(eli.getManifestationRoot())
            .build();

    this.frbrDate =
        DateLeaf.builder()
            .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrdate-n1")
            .date(eli.pointInTimeManifestation().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .name("generierung")
            .build();

    this.frbrAuthor =
        HrefLeaf.builder()
            .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrauthor-n1")
            .href("recht.bund.de")
            .build();

    this.frbrFormat =
        FormatLeaf.builder()
            .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrformat-n1")
            .value(eli.format())
            .build();
  }

  /**
   * Sets the {@code FRBRthis} element.
   *
   * @param manifestationEli the manifestation ELI value
   * @return this {@link FRBRManifestation} for chaining
   */
  public FRBRManifestation setThis(String manifestationEli) {
    this.frbrThis =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrmanifestation-n1_frbrthis-n1")
            .value(manifestationEli)
            .build();

    return this;
  }
}
