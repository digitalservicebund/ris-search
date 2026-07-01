package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Reusable FRBR leaf-element types shared by FRBRWork, FRBRExpression, and FRBRManifestation. */
final class FRBRLeafs {

  private FRBRLeafs() {}

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class ValueLeaf extends BaseElement {

    @XmlAttribute(name = "eId")
    String eId;

    @XmlAttribute(name = "value")
    String value;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class NamedLeaf extends BaseElement {

    @XmlAttribute(name = "eId")
    String eId;

    @XmlAttribute(name = "name")
    String name;

    @XmlAttribute(name = "value")
    String value;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class DateLeaf extends BaseElement {

    @XmlAttribute(name = "eId")
    String eId;

    @XmlAttribute(name = "date")
    String date;

    @XmlAttribute(name = "name")
    String name;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class HrefLeaf extends BaseElement {

    @XmlAttribute(name = "eId")
    String eId;

    @XmlAttribute(name = "href")
    String href;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class LanguageLeaf extends BaseElement {

    @XmlAttribute(name = "eId")
    String eId;

    @XmlAttribute(name = "language")
    String language;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class FormatLeaf extends BaseElement {

    @XmlAttribute(name = "eId")
    String eId;

    @XmlAttribute(name = "value")
    String value;
  }
}
