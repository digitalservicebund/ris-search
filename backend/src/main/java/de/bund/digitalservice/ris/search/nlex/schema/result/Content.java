package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the main content structure of a document in the search schema.
 *
 * <p>This class is primarily used to model textual and structural information of a document's
 * content, including its language, title, and a list of paragraphs (represented by {@code Para}
 * objects).
 *
 * <p>Fields: - {@code lang}: Language metadata for the content. This is optional and adheres to the
 * standard language code format (e.g., "de-DE"). - {@code title}: The title of the content section,
 * represented as a string. - {@code paraList}: A list of paragraphs, where each paragraph is an
 * instance of the {@code Para} class.
 *
 * <p>Constants: - {@code LANG_DE_DE}: A predefined constant representing the German language in the
 * "de-DE" locale format.
 *
 * <p>The {@code Content} class is used within the {@code Document} class to encapsulate content
 * information.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Content {

  public static final String LANG_DE_DE = "de-DE";

  @XmlAttribute private String lang;

  @XmlElement private String title;

  @XmlElement(name = "para")
  private List<Para> paraList;
}
