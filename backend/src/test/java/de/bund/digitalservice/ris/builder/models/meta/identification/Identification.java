package de.bund.digitalservice.ris.builder.models.meta.identification;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Identification extends BaseElement {

  public static Identification fromEli(String eliString) {
    Optional<EliFile> parsedEli = EliFile.fromString(eliString);
    if (parsedEli.isEmpty()) {
      throw new IllegalArgumentException("Invalid Eli");
    }

    EliFile eli = parsedEli.get();

    Identification.IdentificationBuilder builder = Identification.builder();
    builder.frbrWork(FRBRWork.fromEli(eli));
    builder.frbrExpression(FRBRExpression.fromEli(eli));
    builder.frbrManifestation(FRBRManifestation.fromEli(eli));

    return builder.build();
  }

  @Builder.Default @XmlAttribute private String eId = "meta-n1_ident-n1";

  @Builder.Default @XmlAttribute private String source = "attributsemantik-noch-undefiniert";

  @Builder.Default
  @XmlElement(name = "FRBRWork", namespace = NormTestDataBuilder.AKN_NS)
  private FRBRWork frbrWork = FRBRWork.builder().build();

  @Builder.Default
  @XmlElement(name = "FRBRExpression", namespace = NormTestDataBuilder.AKN_NS)
  private FRBRExpression frbrExpression = FRBRExpression.builder().build();

  @Builder.Default
  @XmlElement(name = "FRBRManifestation", namespace = NormTestDataBuilder.AKN_NS)
  private FRBRManifestation frbrManifestation = FRBRManifestation.builder().build();
}
