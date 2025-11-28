package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Getter;

/**
 * Represents the metadata for RIS (Legal Information System) documents, specifically for
 * unselbstständige (dependent) publications.
 *
 * <p>This class is used to deserialize XML data related to the metadata structure of such
 * publications. It relies on JAXB (Jakarta XML Binding) for mapping XML elements and their
 * attributes to Java object fields.
 *
 * <p>Fields: - yearsOfPublication: A list of publication years that are part of the RIS metadata.
 * Annotated with JAXB annotations to handle XML element wrapping and namespace mapping. -
 * gliederung: A detailed structure (of type Gliederung) that is part of the metadata, representing
 * a hierarchical breakdown of the document's structure.
 */
@Getter
public class RisMeta {
  @XmlElementWrapper(
      name = "veroeffentlichungsJahre",
      namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  @XmlElement(
      name = "veroeffentlichungsJahr",
      namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private List<String> yearsOfPublication;

  @XmlElement(name = "gliederung", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private Gliederung gliederung;
}
