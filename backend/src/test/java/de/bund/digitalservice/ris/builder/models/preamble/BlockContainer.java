package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Heading;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:blockContainer} element holding the table of contents. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockContainer extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "präambel-n1_blockcontainer-n1";

  @Builder.Default @XmlAttribute private String refersTo = "inhaltsuebersicht";

  @Builder.Default
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading =
      Heading.builder()
          .eId("präambel-n1_blockcontainer-n1_überschrift-n1")
          .headline(List.of("Inhaltsverzeichnis"))
          .build();

  @Builder.Default
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Toc toc = Toc.builder().build();
}
