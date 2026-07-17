package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Reusable FRBR leaf-element types shared by FRBRWork, FRBRExpression, and FRBRManifestation. */
public final class FRBRLeafs {

  private FRBRLeafs() {}

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Setter
  public static class ValueLeaf extends BaseElement {

    @XmlAttribute String eId;
    @XmlAttribute String value;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class NamedLeaf extends BaseElement {

    @XmlAttribute String eId;

    @XmlAttribute String name;

    @XmlAttribute String value;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class DateLeaf extends BaseElement {

    @XmlAttribute String eId;

    @XmlAttribute String date;

    @XmlAttribute String name;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class HrefLeaf extends BaseElement {

    @XmlAttribute String eId;

    @XmlAttribute String href;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class LanguageLeaf extends BaseElement {

    @XmlAttribute String eId;

    @XmlAttribute String language;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class FormatLeaf extends BaseElement {

    @XmlAttribute String eId;

    @XmlAttribute String value;
  }
}
