package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;

/**
 * Abstract base class representing a block element in the AKN (Akoma Ntoso) format. This class
 * serves as a parent for various specific block types such as Opinions, HeadNotes, Outlines, etc.
 */
@NoArgsConstructor
@XmlDiscriminatorNode("@name")
@XmlSeeAlso({
  Opinions.class,
  AknEmbeddedStructureInBlock.HeadNote.class,
  AknEmbeddedStructureInBlock.OtherHeadNote.class,
  AknEmbeddedStructureInBlock.Outline.class,
  AknEmbeddedStructureInBlock.Tenor.class,
  AknEmbeddedStructureInBlock.DecisionReasons.class,
  AknEmbeddedStructureInBlock.Reasons.class,
  AknEmbeddedStructureInBlock.OtherLongText.class
})
public abstract class AknBlock {
  public abstract String getName();

  public abstract JaxbHtml getContent();
}
