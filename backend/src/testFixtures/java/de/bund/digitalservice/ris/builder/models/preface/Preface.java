package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknDate;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Block;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:preface} element, holding the norm's title and legislation date. */
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Preface extends BaseElement {

  @XmlAttribute private String eId = "einleitung-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  @Getter
  private LongTitle longTitle = new LongTitle();

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Block block = buildLegislationDateBlock("2002-01-01");

  /** Creates a Preface with the given block, without a long title. */
  public Preface(Block block) {
    this.longTitle = null;
    this.block = block;
  }

  public void setLegislationDate(String date) {
    this.block = buildLegislationDateBlock(date);
  }

  private static Block buildLegislationDateBlock(String date) {
    return new Block(
        "einleitung-n1_block-n1",
        List.of(new AknDate("einleitung-n1_block-n1_datum-n1", "ausfertigung-datum", date)));
  }
}
