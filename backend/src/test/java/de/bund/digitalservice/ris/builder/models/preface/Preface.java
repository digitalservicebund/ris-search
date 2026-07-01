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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class Preface extends BaseElement {

  @Builder.Default
  @XmlAttribute(name = "eId")
  private String eId = "einleitung-n1";

  @Builder.Default
  @XmlElement(name = "longTitle", namespace = NormTestDataBuilder.AKN_NS)
  private LongTitle longTitle = LongTitle.builder().build();

  @Builder.Default
  @XmlElement(name = "block", namespace = NormTestDataBuilder.AKN_NS)
  private Block block = buildLegislationDateBlock("2002-01-01");

  public void setLegislationDate(String date) {
    this.block = buildLegislationDateBlock(date);
  }

  private static Block buildLegislationDateBlock(String date) {
    return Block.builder()
        .eId("einleitung-n1_block-n1")
        .children(
            List.of(
                AknDate.builder()
                    .eId("einleitung-n1_block-n1_datum-n1")
                    .refersTo("ausfertigung-datum")
                    .date(date)
                    .build()))
        .build();
  }
}
