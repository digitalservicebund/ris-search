package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Heading;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:blockContainer} element holding the table of contents. */
@NoArgsConstructor
public class BlockContainer extends BaseElement {

  @XmlAttribute private String eId = "präambel-n1_blockcontainer-n1";

  @XmlAttribute private String refersTo = "inhaltsuebersicht";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading =
      new Heading("präambel-n1_blockcontainer-n1_überschrift-n1", List.of("Inhaltsverzeichnis"));

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Toc toc = new Toc();

  public BlockContainer(Toc toc) {
    this.toc = toc;
  }
}
