package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a collection of other references used within an administrative directive, specifically
 * containing a list of implicit references.
 *
 * <p>Each implicit reference is represented by an instance of the `ImplicitReference` class and
 * provides details about related normative and case law data.
 *
 * <p>The `implicitReferences` field is annotated for XML mapping and corresponds to elements
 * identified by the name "implicitReference" in the specified namespace defined within
 * `AdministrativeDirectiveLdml.AKN_NS`.
 */
@Getter
@Setter
public class OtherReferences {

  @XmlElement(name = "implicitReference", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private List<ImplicitReference> implicitReferences;
}
