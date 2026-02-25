package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.oxm.annotations.XmlPath;

@Getter
@Setter
public class DocumentaryShortTexts {

  @XmlElement(name = "titelzeile", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private TitleLine titleLine;

  @XmlElement(name = "orientierungssatz", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Orientierungssatz headNotes;

  @XmlElement(name = "sonstigerOrientierungssatz", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private SonstigerOrientierungssatz otherHeadNotes;

  @Getter
  @Setter
  public static class TitleLine {
    @XmlPath(".")
    private JaxbHtml content;
  }

  @Getter
  @Setter
  public static class Orientierungssatz {
    @XmlPath(".")
    private JaxbHtml content;
  }

  @Getter
  @Setter
  public static class SonstigerOrientierungssatz {
    @XmlPath(".")
    private JaxbHtml content;
  }
}
