package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Represents a generic container block within an Akoma Ntoso document. *
 *
 * <p>In the context of case law, a block is often used to group related elements, such as a
 * collection of judicial opinions or dissenting views, under a specific named category.
 */
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "block", namespace = CaseLawLdmlNamespaces.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class Block {

  /**
   * The name or identifier of the block, defining its type or purpose (e.g., indicating the type of
   * legal reasoning or section).
   */
  @XmlAttribute(name = "name")
  private String name;

  /**
   * A list of judicial opinions contained within this block. *
   *
   * <p>Mapped to the Akoma Ntoso namespace, representing the individual perspectives or rulings
   * issued by the court.
   */
  @XmlElement(name = "opinion", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<Opinion> opinions;
}
