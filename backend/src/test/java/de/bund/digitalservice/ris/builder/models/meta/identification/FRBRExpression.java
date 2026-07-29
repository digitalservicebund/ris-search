package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.DateLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.HrefLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.LanguageLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.NamedLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.ValueLeaf;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents the {@code akn:FRBRExpression} element identifying the norm's expression level. */
public class FRBRExpression extends BaseElement {

  @XmlAttribute private final String eId;

  @XmlElement(name = "FRBRthis", namespace = NormTestDataBuilder.AKN_NS)
  private final ValueLeaf frbrThis;

  @XmlElement(name = "FRBRuri", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrUri;

  @XmlElement(name = "FRBRalias", namespace = NormTestDataBuilder.AKN_NS)
  private final NamedLeaf frbrAlias;

  @XmlElement(name = "FRBRauthor", namespace = NormTestDataBuilder.AKN_NS)
  private final HrefLeaf frbrAuthor;

  @XmlElement(name = "FRBRdate", namespace = NormTestDataBuilder.AKN_NS)
  private final DateLeaf frbrDate;

  @XmlElement(name = "FRBRlanguage", namespace = NormTestDataBuilder.AKN_NS)
  private final LanguageLeaf frbrLanguage;

  @XmlElement(name = "FRBRversionNumber", namespace = NormTestDataBuilder.AKN_NS)
  private final ValueLeaf frbrVersionNumber;

  /** FRBRExpression empty constructor. Calls another constructor with default values. */
  public FRBRExpression() {
    // eli/bund/bgbl-1/2025/341/2025-12-22/1/deu/regelungstext-verkuendung-1
    this(
        new ExpressionEli("bund", "bgbl-1", "2025", "341", LocalDate.of(2025, 12, 22), 1, "deu"),
        "regelungstext-verkuendung-1");
  }

  /**
   * FRBRExpression constructor. Builds a FRBRExpression from an ExpressionEli and a file name.
   *
   * @param eli the expression eli for the FRBRExpression
   * @param fileName the file name component (subtype identifier, e.g.
   *     "regelungstext-verkuendung-1")
   */
  public FRBRExpression(ExpressionEli eli, String fileName) {

    this.eId = "meta-n1_ident-n1_frbrexpression-n1";

    this.frbrThis =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbrthis-n1")
            .value(eli + "/" + fileName)
            .build();

    this.frbrUri =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbruri-n1")
            .value(eli.toString())
            .build();

    this.frbrAlias =
        NamedLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbralias-n1")
            .name("aktuelle-version-id")
            .value("27204f61-13d2-5943-ad65-83411724b996")
            .build();

    this.frbrAuthor =
        HrefLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbrauthor-n1")
            .href("recht.bund.de/institution/bundesregierung")
            .build();

    this.frbrDate =
        DateLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbrdate-n1")
            .date(eli.pointInTime().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .name("verkuendung")
            .build();

    this.frbrLanguage =
        LanguageLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbrlanguage-n1")
            .language(eli.language())
            .build();

    this.frbrVersionNumber =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbrversionnumber-n1")
            .value(String.valueOf(eli.version()))
            .build();
  }

  /**
   * Sets the {@code FRBRuri} element.
   *
   * @param expressionEli the expression ELI value
   * @return this {@link FRBRExpression} for chaining
   */
  public FRBRExpression setUri(String expressionEli) {
    this.frbrUri =
        ValueLeaf.builder()
            .eId("meta-n1_ident-n1_frbrexpression-n1_frbruri-n1")
            .value(expressionEli)
            .build();

    return this;
  }
}
