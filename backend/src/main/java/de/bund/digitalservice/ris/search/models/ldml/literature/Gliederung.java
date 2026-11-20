package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents the hierarchical structure or division (Gliederung) of a document's content,
 * specifically for unselbstständige (dependent) literature publications in the RIS (Legal
 * Information System) context.
 *
 * <p>This class is designed to represent and facilitate the deserialization of the "gliederung"
 * element in XML structures. It utilizes JAXB (Jakarta XML Binding) for mapping XML elements into
 * Java objects.
 *
 * <p>Fields: - gliederungEntry: A list of entries representing individual elements within the
 * hierarchical structure. These entries are serialized under the "gliederungseintrag" XML tag and
 * are scoped within the "RIS_NS" namespace.
 */
@Getter
public class Gliederung {

  @XmlElement(name = "gliederungseintrag", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> gliederungEntry;
}
