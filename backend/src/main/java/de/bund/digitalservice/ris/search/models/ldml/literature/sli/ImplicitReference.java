package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.FundstelleSelbstaendig;
import de.bund.digitalservice.ris.search.models.ldml.literature.FundstelleUnselbstaendig;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import de.bund.digitalservice.ris.search.models.ldml.literature.NormReference;
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
 * reference to unselbstaendig literature (`fundstelleUnselbstaendig`). - A reference to
 * selbstaendig literature (`fundstelleSelbstaendig`). - A reference to normative elements
 * (`normReference`), including potential legal or regulatory norms.
 */
@Getter
public class ImplicitReference {

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(
      name = "fundstelleUnselbstaendig",
      namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private FundstelleUnselbstaendig fundstelleUnselbstaendig;

  @XmlElement(
      name = "fundstelleSelbstaendig",
      namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private FundstelleSelbstaendig fundstelleSelbstaendig;

  @XmlElement(name = "normReference", namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private NormReference normReference;
}
