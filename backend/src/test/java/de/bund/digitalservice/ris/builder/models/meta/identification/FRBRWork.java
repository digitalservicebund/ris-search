package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.DateLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.HrefLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.NamedLeaf;
import de.bund.digitalservice.ris.builder.models.meta.identification.FRBRLeafs.ValueLeaf;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** Represents the {@code akn:FRBRWork} element identifying the norm's work level. */
public class FRBRWork extends BaseElement {

  @XmlAttribute private final String eId;

  @XmlElement(name = "FRBRthis", namespace = NormTestDataBuilder.AKN_NS)
  private final ValueLeaf frbrThis;

  @XmlElement(name = "FRBRuri", namespace = NormTestDataBuilder.AKN_NS)
  @Getter
  private final ValueLeaf frbrUri;

  @XmlElement(name = "FRBRalias", namespace = NormTestDataBuilder.AKN_NS)
  private final NamedLeaf frbrAlias;

  @XmlElement(name = "FRBRdate", namespace = NormTestDataBuilder.AKN_NS)
  private DateLeaf frbrDate;

  @XmlElement(name = "FRBRauthor", namespace = NormTestDataBuilder.AKN_NS)
  private final HrefLeaf frbrAuthor;

  @XmlElement(name = "FRBRcountry", namespace = NormTestDataBuilder.AKN_NS)
  private final ValueLeaf frbrCountry;

  @XmlElement(name = "FRBRnumber", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrNumber;

  @XmlElement(name = "FRBRname", namespace = NormTestDataBuilder.AKN_NS)
  private ValueLeaf frbrName;

  @XmlElement(name = "FRBRsubtype", namespace = NormTestDataBuilder.AKN_NS)
  private final ValueLeaf frbrSubtype;

  /** FRBRWork empty constructor. Calls another constructor with default values. */
  public FRBRWork() {
    // eli/bund/bgbl-1/2025/341/regelungstext-verkuendung-1
    this(new WorkEli("bund", "bgbl-1", "2025", "341"));
  }

  /**
   * FRBRWork constructor. Builds a FRBRWork from a WorkEli
   *
   * @param eli the work eli for the FRBRWork
   */
  public FRBRWork(WorkEli eli) {

    this.eId = "meta-n1_ident-n1_frbrwork-n1";

    this.frbrThis =
        new ValueLeaf(
            "meta-n1_ident-n1_frbrwork-n1_frbrthis-n1",
            eli.toString() + "/regelungstext-verkuendung-1");

    this.frbrUri = new ValueLeaf("meta-n1_ident-n1_frbrwork-n1_frbruri-n1", eli.toString());

    this.frbrAlias =
        new NamedLeaf(
            "meta-n1_ident-n1_frbrwork-n1_frbralias-n1",
            "übergreifende-id",
            "b29e7271-d0eb-5ae1-9513-27af3a7e69dd");

    this.frbrDate =
        new DateLeaf(
            "meta-n1_ident-n1_frbrwork-n1_frbrdate-n1",
            "2025-12-22",
            "verkuendungsfassung-verkuendungsdatum");

    this.frbrAuthor =
        new HrefLeaf(
            "meta-n1_ident-n1_frbrwork-n1_frbrauthor-n1",
            "recht.bund.de/institution/bundesregierung");

    this.frbrCountry = new ValueLeaf("meta-n1_ident-n1_frbrwork-n1_frbrcountry-n1", "de");

    this.frbrNumber =
        new ValueLeaf("meta-n1_ident-n1_frbrwork-n1_frbrnumber-n1", eli.naturalIdentifier());

    this.frbrName = new ValueLeaf("meta-n1_ident-n1_frbrwork-n1_frbrname-n1", eli.agent());

    this.frbrSubtype =
        new ValueLeaf("meta-n1_ident-n1_frbrwork-n1_frbrsubtype-n1", "regelungstext-verkuendung-1");
  }

  /**
   * Sets the {@code FRBRname} element.
   *
   * @param name the authority/name value
   * @return this {@link FRBRWork} for chaining
   */
  public FRBRWork setName(String name) {
    this.frbrName = new ValueLeaf("meta-n1_ident-n1_frbrwork-n1_frbrname-n1", name);
    return this;
  }

  /**
   * Sets the {@code FRBRnumber} element.
   *
   * @param number the document number value
   * @return this {@link FRBRWork} for chaining
   */
  public FRBRWork setNumber(String number) {
    this.frbrNumber = new ValueLeaf("meta-n1_ident-n1_frbrwork-n1_frbrnumber-n1", number);
    return this;
  }

  /**
   * Sets the {@code FRBRdate} element for the publication date.
   *
   * @param date the publication date value
   * @return this {@link FRBRWork} for chaining
   */
  public FRBRWork setDatePublished(String date) {
    this.frbrDate =
        new DateLeaf(
            "meta-n1_ident-n1_frbrwork-n1_frbrdate-n1",
            date,
            "verkuendungsfassung-verkuendungsdatum");
    return this;
  }
}
