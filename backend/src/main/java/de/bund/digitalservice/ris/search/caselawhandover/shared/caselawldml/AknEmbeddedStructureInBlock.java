package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

/** Represents an embedded structure within a block in a case law LDML document. */
@NoArgsConstructor
@Getter
public abstract class AknEmbeddedStructureInBlock extends AknBlock {

  @XmlElement(name = "embeddedStructure", namespace = CaseLawLdmlNamespaces.AKN_NS)
  protected JaxbHtml content;

  public abstract String getName();

  /** Orientierungssatz */
  @NoArgsConstructor
  @XmlDiscriminatorValue(HeadNote.NAME)
  public static class HeadNote extends AknEmbeddedStructureInBlock {
    public static final String NAME = "Orientierungssatz";

    public HeadNote(JaxbHtml content) {
      this.content = content;
    }

    public static HeadNote build(JaxbHtml content) {
      return content == null ? null : new HeadNote(content);
    }

    public String getName() {
      return NAME;
    }
  }

  /** Sonstiger Orientierungssatz */
  @NoArgsConstructor
  @XmlDiscriminatorValue(OtherHeadNote.NAME)
  public static class OtherHeadNote extends AknEmbeddedStructureInBlock {
    public static final String NAME = "Sonstiger Orientierungssatz";

    public OtherHeadNote(JaxbHtml content) {
      this.content = content;
    }

    public static OtherHeadNote build(JaxbHtml content) {
      return content == null ? null : new OtherHeadNote(content);
    }

    public String getName() {
      return NAME;
    }
  }

  /** Gliederung */
  @NoArgsConstructor
  @XmlDiscriminatorValue(Outline.NAME)
  public static class Outline extends AknEmbeddedStructureInBlock {
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

  /** Tenor */
  @NoArgsConstructor
  @XmlDiscriminatorValue(Tenor.NAME)
  public static class Tenor extends AknEmbeddedStructureInBlock {
    public static final String NAME = "Tenor";

    public Tenor(JaxbHtml content) {
      this.content = content;
    }

    public static Tenor build(JaxbHtml content) {
      return content == null ? null : new Tenor(content);
    }

    public String getName() {
      return NAME;
    }
  }

  /** Entscheidungsgründe */
  @NoArgsConstructor
  @XmlDiscriminatorValue(DecisionReasons.NAME)
  public static class DecisionReasons extends AknEmbeddedStructureInBlock {
    public static final String NAME = "Entscheidungsgründe";

    public DecisionReasons(JaxbHtml content) {
      this.content = content;
    }

    public static DecisionReasons build(JaxbHtml content) {
      return content == null ? null : new DecisionReasons(content);
    }

    public String getName() {
      return NAME;
    }
  }

  /** Gründe */
  @NoArgsConstructor
  @XmlDiscriminatorValue(Reasons.NAME)
  public static class Reasons extends AknEmbeddedStructureInBlock {
    public static final String NAME = "Gründe";

    public Reasons(JaxbHtml content) {
      this.content = content;
    }

    public static Reasons build(JaxbHtml content) {
      return content == null ? null : new Reasons(content);
    }

    public String getName() {
      return NAME;
    }
  }

  /** Sonstiger Langtext */
  @NoArgsConstructor
  @XmlDiscriminatorValue(OtherLongText.NAME)
  public static class OtherLongText extends AknEmbeddedStructureInBlock {
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
}
