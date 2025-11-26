package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the Classification element in the case law LDML format. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Classification {
  @Builder.Default
  @XmlAttribute(name = "source")
  private String name = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "keyword", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<AknKeyword> keyword;
}
