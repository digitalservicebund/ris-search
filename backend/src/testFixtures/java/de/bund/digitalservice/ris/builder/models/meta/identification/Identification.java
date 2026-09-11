package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents the {@code akn:identification} element, grouping the FRBR
 * work/expression/manifestation.
 */
@NoArgsConstructor
@Getter
public class Identification extends BaseElement {
  @XmlAttribute private String eId = "meta-n1_ident-n1";

  @XmlAttribute private String source = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "FRBRWork", namespace = NormTestDataBuilder.AKN_NS)
  private FRBRWork frbrWork = new FRBRWork();

  @XmlElement(name = "FRBRExpression", namespace = NormTestDataBuilder.AKN_NS)
  private FRBRExpression frbrExpression = new FRBRExpression();

  @XmlElement(name = "FRBRManifestation", namespace = NormTestDataBuilder.AKN_NS)
  private FRBRManifestation frbrManifestation = new FRBRManifestation();

  /**
   * Builds an Identification from the given manifestation ELI string.
   *
   * @param eliString the manifestation ELI, e.g.
   *     "eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml"
   * @return {@link Identification}
   * @throws IllegalArgumentException if the ELI string cannot be parsed
   */
  public Identification(String eliString) {
    Optional<EliFile> parsedEli = EliFile.fromString(eliString);
    if (parsedEli.isEmpty()) {
      throw new IllegalArgumentException("Invalid Eli");
    }

    EliFile eli = parsedEli.get();

    this.frbrWork = new FRBRWork(eli.getWorkEli());
    this.frbrExpression = new FRBRExpression(eli.getExpressionEli(), eli.fileName());
    this.frbrManifestation = new FRBRManifestation(eli.getManifestationEli());
  }
}
