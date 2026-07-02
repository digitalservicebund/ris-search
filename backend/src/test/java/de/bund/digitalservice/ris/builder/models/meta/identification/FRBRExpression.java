package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.DateLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.HrefLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.LanguageLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.NamedLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.ValueLeaf;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FRBRExpression extends BaseElement {

  public static FRBRExpression fromEli(EliFile eli) {
    return FRBRExpression.builder()
        .frbrThis(
            ValueLeaf.builder()
                .eId("meta-n1_ident-n1_frbrexpression-n1_frbrthis-n1")
                .value(eli.getExpressionEli() + "/" + eli.fileName())
                .build())
        .frbrUri(
            ValueLeaf.builder()
                .eId("meta-n1_ident-n1_frbrexpression-n1_frbruri-n1")
                .value(eli.getExpressionEli().toString())
                .build())
        .frbrDate(
            DateLeaf.builder()
                .eId("meta-n1_ident-n1_frbrexpression-n1_frbrdate-n1")
                .date(eli.pointInTime().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .name("verkuendung")
                .build())
        .frbrLanguage(
            LanguageLeaf.builder()
                .eId("meta-n1_ident-n1_frbrexpression-n1_frbrlanguage-n1")
                .language(eli.language())
                .build())
        .frbrVersionNumber(
            ValueLeaf.builder()
                .eId("meta-n1_ident-n1_frbrexpression-n1_frbrversionnumber-n1")
                .value(String.valueOf(eli.version()))
                .build())
        .build();
  }

  @Builder.Default @XmlAttribute private String eId = "meta-n1_ident-n1_frbrexpression-n1";

  @Builder.Default
  @XmlElement(name = "FRBRthis", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrThis =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrexpression-n1_frbrthis-n1")
          .value("eli/bund/bgbl-1/2025/341/2025-12-22/1/deu/regelungstext-verkuendung-1")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRuri", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrUri =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrexpression-n1_frbruri-n1")
          .value("eli/bund/bgbl-1/2025/341/2025-12-22/1/deu")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRalias", namespace = NormTestDataBuilder.AKN_NS)
  private NamedLeaf frbrAlias =
      NamedLeaf.builder()
          .eId("meta-n1_ident-n1_frbrexpression-n1_frbralias-n1")
          .name("aktuelle-version-id")
          .value("27204f61-13d2-5943-ad65-83411724b996")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRauthor", namespace = NormTestDataBuilder.AKN_NS)
  private HrefLeaf frbrAuthor =
      HrefLeaf.builder()
          .eId("meta-n1_ident-n1_frbrexpression-n1_frbrauthor-n1")
          .href("recht.bund.de/institution/bundesregierung")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRdate", namespace = NormTestDataBuilder.AKN_NS)
  private DateLeaf frbrDate =
      DateLeaf.builder()
          .eId("meta-n1_ident-n1_frbrexpression-n1_frbrdate-n1")
          .date("2025-12-22")
          .name("verkuendung")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRlanguage", namespace = NormTestDataBuilder.AKN_NS)
  private LanguageLeaf frbrLanguage =
      LanguageLeaf.builder()
          .eId("meta-n1_ident-n1_frbrexpression-n1_frbrlanguage-n1")
          .language("deu")
          .build();

  @Builder.Default
  @XmlElement(name = "FRBRversionNumber", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrVersionNumber =
      ValueLeaf.builder()
          .eId("meta-n1_ident-n1_frbrexpression-n1_frbrversionnumber-n1")
          .value("1")
          .build();
}
