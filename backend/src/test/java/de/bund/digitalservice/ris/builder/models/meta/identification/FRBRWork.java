package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.DateLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.HrefLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.NamedLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.ValueLeaf;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FRBRWork extends BaseElement {

  public static FRBRWork fromEli(EliFile eli) {

    FRBRWork.FRBRWorkBuilder builder = FRBRWork.builder();
    builder.frbrThis(
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrwork-n1_frbrthis-n1")
            .value(eli.getWorkEli().toString())
            .build());

    builder.frbrUri(
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrwork-n1_frbruri-n1")
            .value(
                "eli/"
                    + eli.jurisdiction()
                    + "/"
                    + eli.agent()
                    + "/"
                    + eli.year()
                    + "/"
                    + eli.naturalIdentifier())
            .build());

    builder.frbrNumber(
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrwork-n1_frbrnumber-n1")
            .value(eli.naturalIdentifier())
            .build());

    builder.frbrName(
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrwork-n1_frbrname-n1")
            .value(eli.agent())
            .build());

    builder.frbrSubtype(
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrwork-n1_frbrsubtype-n1")
            .value(eli.fileName())
            .build());

    return builder.build();
  }

  public void setDatePublished(String date) {
    this.frbrDate =
        DateLeaf.builder()
            .eId("meta-n1_ident-n1_frbrwork-n1_frbrdate-n1")
            .date(date)
            .name("verkuendungsfassung-verkuendungsdatum")
            .build();
  }

  @Builder.Default @XmlAttribute private String eId = "meta-n1_ident-n1_frbrwork-n1";

  @Builder.Default
  @XmlElement(name = "FRBRthis", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrThis =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrwork-n1_frbrthis-n1")
          .value("eli/bund/bgbl-1/2025/341/regelungstext-verkuendung-1")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRuri", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrUri =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrwork-n1_frbruri-n1")
          .value("eli/bund/bgbl-1/2025/341")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRalias", namespace = NormTestDataBuilder.AKN_NS)
  private NamedLeaf frbrAlias =
      NamedLeaf.builder()
          .eId("meta-n1_ident-n1_frbrwork-n1_frbralias-n1")
          .name("übergreifende-id")
          .value("b29e7271-d0eb-5ae1-9513-27af3a7e69dd")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRdate", namespace = NormTestDataBuilder.AKN_NS)
  private DateLeaf frbrDate =
      DateLeaf.builder()
          .eId("meta-n1_ident-n1_frbrwork-n1_frbrdate-n1")
          .date("2025-12-22")
          .name("verkuendungsfassung-verkuendungsdatum")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRauthor", namespace = NormTestDataBuilder.AKN_NS)
  private HrefLeaf frbrAuthor =
      HrefLeaf.builder()
          .eId("meta-n1_ident-n1_frbrwork-n1_frbrauthor-n1")
          .href("recht.bund.de/institution/bundesregierung")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRcountry", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrCountry =
      ValueLeaf.builder().eId("meta-n1_ident-n1_frbrwork-n1_frbrcountry-n1").value("de").build();

  @Builder.Default
  @XmlElement(name = "FRBRnumber", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrNumber =
      ValueLeaf.builder().eId("meta-n1_ident-n1_frbrwork-n1_frbrnumber-n1").value("341").build();

  @Builder.Default
  @XmlElement(name = "FRBRname", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrName =
      ValueLeaf.builder().eId("meta-n1_ident-n1_frbrwork-n1_frbrname-n1").value("bgbl-1").build();

  @Builder.Default
  @XmlElement(name = "FRBRsubtype", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrSubtype =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrwork-n1_frbrsubtype-n1")
          .value("regelungstext-verkuendung-1")
          .build();
}
