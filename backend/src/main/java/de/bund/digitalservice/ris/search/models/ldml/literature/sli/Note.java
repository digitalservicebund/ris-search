package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents the FRBR (Functional Requirements for Bibliographic Records) expression level in the
 * bibliographic hierarchy.
 *
 * <p>This class is utilized for handling footnotes and endnotes
 *
 * <p>Fields: - block: a text block container (represented by the FrbrLanguage block)
 */
@Getter
public class Note {

  @XmlElement(namespace = LiteratureNamespaces.AKN_NS)
  Block block;
}
