package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import static de.bund.digitalservice.ris.search.models.ldml.caselaw.CaseLawLdmlNamespaces.RIS_NS;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * The abstract base class for all main content sections within an Akoma Ntoso (AKN) document.
 *
 * <p>This class enables polymorphic handling of different legal text sections. By using EclipseLink
 * MOXy's {@link XmlDiscriminatorNode}, the specific subclass is determined at runtime based on the
 * value of the {@code ris:domainTerm} attribute in the XML.
 *
 * @see AknMainContentIntroduction
 * @see AknMainContentMotivation
 */
@NoArgsConstructor
@Getter
public class AknMainContent {

  /**
   * The actual HTML-formatted legal reasoning text.
   *
   * <p>Mapped to the current node content via {@link
   * org.eclipse.persistence.oxm.annotations.XmlPath}.
   */
  @XmlPath(".")
  private JaxbHtml content;

  @XmlAttribute(namespace = RIS_NS, name = "domainTerm")
  private String domainTerm;
}
