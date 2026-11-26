package de.bund.digitalservice.ris.search.models.ldml.literature;

import de.bund.digitalservice.ris.search.models.ldml.Fundstelle;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzVerwaltungsvorschrift;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents an implicit reference within a larger document structure. This class provides
 * references to different elements such as unselbstaendig or selbstaendig literals and norm
 * references, with the ability to include metadata about how the reference should be displayed.
 *
 * <p>Fields in this class are annotated to support XML serialization, enabling integration with
 * XML-based document or metadata standards.
 *
 * <p>The following elements are captured: - Display attributes for the implicit reference. - A
 * Fundstelle in a periodical (`fundstelle`, ULI only). - A Fundstelle in selbstständige literature
 * (`fundstelleSelbststaendig`, ULI only). - A reference to normative elements (`normReference`),
 * including potential legal or regulatory norms.
 */
@Getter
public class ImplicitReference {

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(name = "fundstelle", namespace = LiteratureNamespaces.RIS_NS)
  private Fundstelle fundstelle;

  @XmlElement(name = "fundstelleSelbststaendig", namespace = LiteratureNamespaces.RIS_NS)
  private FundstelleSelbststaendig fundstelleSelbststaendig;

  @XmlElement(name = "normReference", namespace = LiteratureNamespaces.RIS_NS)
  private NormReference normReference;

  @XmlElement(name = "referenzRechtsprechung", namespace = LiteratureNamespaces.RIS_NS)
  private ReferenzRechtsprechung referenzRechtsprechung;

  @XmlElement(name = "referenzVerwaltungsvorschrift", namespace = LiteratureNamespaces.RIS_NS)
  private ReferenzVerwaltungsvorschrift referenzVerwaltungsvorschrift;

  @XmlElement(name = "referenzSelbststaendigeLiteratur", namespace = LiteratureNamespaces.RIS_NS)
  private ReferenzSelbststaendigeLiteratur referenzSelbststaendigeLiteratur;

  @XmlElement(name = "referenzUnselbststaendigeLiteratur", namespace = LiteratureNamespaces.RIS_NS)
  private ReferenzUnselbststaendigeLiteratur referenzUnselbststaendigeLiteratur;
}
