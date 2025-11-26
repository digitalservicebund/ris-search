package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

/** Represents the Opinions element in the case law LDML format. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@XmlDiscriminatorValue(Opinions.NAME)
public class Opinions extends AknBlock {
  public static final String NAME = "Abweichende Meinung";

  @XmlElement(name = "opinion", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private Opinion opinion;

  /**
   * Builds an Opinions object if the content list is not null or empty or all nulls.
   *
   * @param content the list of content objects for the opinion
   * @return an Opinions object or null if the content is null, empty, or all
   */
  public static Opinions build(List<Object> content) {
    if (content == null || content.isEmpty() || content.stream().allMatch(Objects::isNull)) {
      return null;
    }
    return new Opinions(new Opinion(content));
  }

  public String getName() {
    return Opinions.NAME;
  }

  public JaxbHtml getContent() {
    return opinion.getContent();
  }
}
