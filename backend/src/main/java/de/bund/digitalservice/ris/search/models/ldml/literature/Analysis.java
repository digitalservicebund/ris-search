package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Analysis {

  @XmlElement(name = "otherReferences", namespace = LiteratureNamespaces.AKN_NS)
  private List<OtherReferences> otherReferencesList;

  public List<FundstelleUnselbstaendig> getFundstelleUnselbstaendigList() {
    return extractReferences(ImplicitReference::getFundstelleUnselbstaendig);
  }

  public List<FundstelleSelbstaendig> getFundstelleSelbstaendigList() {
    return extractReferences(ImplicitReference::getFundstelleSelbstaendig);
  }

  private <T> List<T> extractReferences(Function<ImplicitReference, T> getter) {
    return otherReferencesList.stream()
        .flatMap(
            otherReferences ->
                otherReferences.getImplicitReferences().stream()
                    .map(getter)
                    .filter(Objects::nonNull))
        .toList();
  }
}
