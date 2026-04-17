package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/** Container for short documentary and editorial texts associated with a legal decision. */
@Getter
@Setter
public class DocumentaryShortTexts {

  @XmlElement(name = "titelzeile", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisTitelzeile risTitelzeile;

  @XmlElement(name = "orientierungssatz", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisOrientierungssatz risOrientierungssatz;

  @XmlElement(name = "sonstigerOrientierungssatz", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisSonstigerOrientierungssatz risSonstigerOrientierungssatz;

  /** List of decision names (Entscheidungsnamen). */
  @XmlElementWrapper(name = "entscheidungsnamen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "entscheidungsname", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<RisEntscheidungsName> risEntscheidungsNames;

  /** Represents the title line (Titelzeile) of the documentary short texts. */
  @Getter
  @Setter
  public static class RisTitelzeile {
    @XmlPath(".")
    private JaxbHtml content;
  }

  /** Represents a primary headnote (Orientierungssatz) of the decision. */
  @Getter
  @Setter
  public static class RisOrientierungssatz {
    @XmlPath(".")
    private JaxbHtml content;
  }

  /** Represents an additional or other headnote (Sonstiger Orientierungssatz). */
  @Getter
  @Setter
  public static class RisSonstigerOrientierungssatz {
    @XmlPath(".")
    private JaxbHtml content;
  }

  /** Represents a single decision name entry. */
  @Getter
  @Setter
  public static class RisEntscheidungsName {
    @XmlPath("text()")
    private String name;
  }
}
