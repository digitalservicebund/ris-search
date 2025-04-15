package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class AknMultipleBlock {

  // Jaxb doesn't handle elements with the same name very well.
  // They need to be in a list with the same class instead of two separate fields.
  // A Map is used with @XmlElement added to a helper getter/setter to facilitate easier use.
  // The Map is LinkedHashMap so that order is preserved when converted to a list.
  // This enables deterministic element order when using the withBlock method
  @XmlTransient private Map<String, AknBlock> blocks = new LinkedHashMap<>();

  @XmlElement(name = "block", namespace = CaseLawLdml.AKN_NS)
  public void setJaxbBlocks(List<AknBlock> blocks) {
    for (AknBlock block : blocks) {
      this.blocks.put(block.getName(), block);
    }
  }

  @XmlElement(name = "block", namespace = CaseLawLdml.AKN_NS)
  public List<AknBlock> getJaxbBlocks() {
    return new ArrayList<>(blocks.values());
  }

  public AknMultipleBlock withBlock(String name, AknBlock block) {
    if (block != null) {
      blocks.put(name, block);
    }
    return this;
  }

  public AknBlock getBlock(String name) {
    return blocks.get(name);
  }
}
