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
import java.time.Month;
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
        new ExpressionEli(
            "bund", "bgbl-1", "2025", "341", LocalDate.of(2025, Month.DECEMBER, 22), 1, "deu"),
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
        new ValueLeaf("meta-n1_ident-n1_frbrexpression-n1_frbrthis-n1", eli + "/" + fileName);

    this.frbrUri = new ValueLeaf("meta-n1_ident-n1_frbrexpression-n1_frbruri-n1", eli.toString());

    this.frbrAlias =
        new NamedLeaf(
            "meta-n1_ident-n1_frbrexpression-n1_frbralias-n1",
            "aktuelle-version-id",
            "27204f61-13d2-5943-ad65-83411724b996");

    this.frbrAuthor =
        new HrefLeaf(
            "meta-n1_ident-n1_frbrexpression-n1_frbrauthor-n1",
            "recht.bund.de/institution/bundesregierung");

    this.frbrDate =
        new DateLeaf(
            "meta-n1_ident-n1_frbrexpression-n1_frbrdate-n1",
            eli.pointInTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
            "verkuendung");

    this.frbrLanguage =
        new LanguageLeaf("meta-n1_ident-n1_frbrexpression-n1_frbrlanguage-n1", eli.language());

    this.frbrVersionNumber =
        new ValueLeaf(
            "meta-n1_ident-n1_frbrexpression-n1_frbrversionnumber-n1",
            String.valueOf(eli.version()));
  }

  /**
   * Sets the {@code FRBRuri} element.
   *
   * @param expressionEli the expression ELI value
   * @return this {@link FRBRExpression} for chaining
   */
  public FRBRExpression setUri(String expressionEli) {
    this.frbrUri = new ValueLeaf("meta-n1_ident-n1_frbrexpression-n1_frbruri-n1", expressionEli);
    return this;
  }
}
