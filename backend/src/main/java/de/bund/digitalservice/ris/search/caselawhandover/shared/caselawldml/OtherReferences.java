package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a collection of other references used within a case law, specifically containing a
 * list of implicit references.
 *
 * <p>Each implicit reference is represented by an instance of the `ImplicitReference` class and
 * provides details about related normative and case law data.
 *
 * <p>The `implicitReferences` field is annotated for XML mapping and corresponds to elements
 * identified by the name "implicitReference" in the specified namespace defined within
 * `CaseLawLdmlNamespaces.AKN_NS`.
 */
@Getter
@Setter
public class OtherReferences {

  @XmlElement(name = "implicitReference", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<ImplicitReference> implicitReferences;

  public <T> List<T> getReferencesByType(Function<ImplicitReference, T> extractor) {
    if (this.getImplicitReferences() == null) {
      return List.of();
    }

    return this.getImplicitReferences().stream()
        .map(extractor) // Extract the specific field (e.g., getFundstelle)
        .filter(Objects::nonNull) // Remove nulls (where the reference wasn't that type)
        .collect(Collectors.toList());
  }
}
