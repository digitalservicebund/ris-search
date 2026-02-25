package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;
import org.eclipse.persistence.oxm.annotations.XmlPath;

@NoArgsConstructor
@Getter
public abstract class AknMainContentIntroduction extends AknMainContent {

  @XmlPath(".")
  protected JaxbHtml content;

  public abstract String getName();

  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentIntroduction.GuidingPrinciple.NAME)
  public static class GuidingPrinciple extends AknMainContentIntroduction {
    public static final String NAME = "Leitsatz";

    public GuidingPrinciple(JaxbHtml content) {
      this.content = content;
    }

    public static GuidingPrinciple build(JaxbHtml content) {
      return content == null ? null : new GuidingPrinciple(content);
    }

    public String getName() {
      return NAME;
    }
  }

  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentIntroduction.Outline.NAME)
  public static class Outline extends AknMainContentIntroduction {
    public static final String NAME = "Gliederung";

    public Outline(JaxbHtml content) {
      this.content = content;
    }

    public static Outline build(JaxbHtml content) {
      return content == null ? null : new Outline(content);
    }

    public String getName() {
      return NAME;
    }
  }
}
