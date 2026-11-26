package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents a definition ({@code ris:definition}) of a literature document.
 *
 * <p>Fields: - definierterBegriff: the defined term.
 */
@Getter
public class RisDefinition {
  @XmlElement(name = "definierterBegriff", namespace = LiteratureNamespaces.RIS_NS)
  private String definierterBegriff;
}
