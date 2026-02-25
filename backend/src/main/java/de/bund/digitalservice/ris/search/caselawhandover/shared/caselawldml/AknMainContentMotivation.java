package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;
import org.eclipse.persistence.oxm.annotations.XmlPath;

@NoArgsConstructor
@Getter
public abstract class AknMainContentMotivation extends AknMainContent {

  @XmlPath(".")
  protected JaxbHtml content;

  public abstract String getName();

  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.DecisionGrounds.NAME)
  public static class DecisionGrounds extends AknMainContentMotivation {
    public static final String NAME = "Entscheidungsgründe";

    public DecisionGrounds(JaxbHtml content) {
      this.content = content;
    }

    public static DecisionGrounds build(JaxbHtml content) {
      return content == null ? null : new DecisionGrounds(content);
    }

    public String getName() {
      return NAME;
    }
  }

  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.Grounds.NAME)
  public static class Grounds extends AknMainContentMotivation {
    public static final String NAME = "Gründe";

    public Grounds(JaxbHtml content) {
      this.content = content;
    }

    public static Grounds build(JaxbHtml content) {
      return content == null ? null : new Grounds(content);
    }

    public String getName() {
      return NAME;
    }
  }

  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.OtherLongText.NAME)
  public static class OtherLongText extends AknMainContentMotivation {
    public static final String NAME = "Sonstiger Langtext";

    public OtherLongText(JaxbHtml content) {
      this.content = content;
    }

    public static OtherLongText build(JaxbHtml content) {
      return content == null ? null : new OtherLongText(content);
    }

    public String getName() {
      return NAME;
    }
  }

  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.DissentingOpinion.NAME)
  public static class DissentingOpinion extends AknMainContentMotivation {
    public static final String NAME = "Abweichende Meinung";

    public DissentingOpinion(JaxbHtml content) {
      this.content = content;
    }

    public static DissentingOpinion build(JaxbHtml content) {
      return content == null ? null : new DissentingOpinion(content);
    }

    public String getName() {
      return NAME;
    }
  }
}
