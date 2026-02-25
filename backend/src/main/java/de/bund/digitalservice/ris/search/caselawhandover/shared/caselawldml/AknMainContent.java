package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;

@NoArgsConstructor
@XmlDiscriminatorNode("@ris:domainTerm")
@XmlSeeAlso({
  AknMainContentIntroduction.GuidingPrinciple.class,
  AknMainContentIntroduction.Outline.class,
  AknMainContentMotivation.DecisionGrounds.class,
  AknMainContentMotivation.Grounds.class,
  AknMainContentMotivation.OtherLongText.class,
  AknMainContentMotivation.DissentingOpinion.class
})
public abstract class AknMainContent {
  public abstract String getName();

  public abstract JaxbHtml getContent();
}
