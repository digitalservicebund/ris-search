package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents metadata for RIS (Regulatory Impact Statements) documents.
 *
 * <p>This class is used to manage and serialize/deserialize metadata elements specific to RIS
 * documents. It includes various properties such as document type, legislation specifics, and
 * references, each annotated for XML processing.
 *
 * <p>Fields:
 *
 * <p>- `documentType`: Represents the type of the document, using the {@link DocumentType} class. -
 * `normgeber`: Represents the legislative authority or norm setter, using the {@link Normgeber}
 * class. - `entryIntoEffectDate`: Represents the date when the document enters into effect. -
 * `expiryDate`: Represents the date when the document expires. - `referenceNumbers`: A list of
 * reference numbers associated with the document. - `activeReferences`: A list of currently active
 * references using the {@link ActiveReference} class. - `fieldsOfLaw`: A list of law fields that
 * the document pertains to, utilizing the {@link FieldOfLaw} class. - `dateToQuoteList`: A list of
 * dates intended for citation purposes. - `tableOfContentsEntries`: A list of entries corresponding
 * to the table of contents for the document.
 *
 * <p>This class relies on JAXB annotations for mapping fields to XML elements and uses Lombok
 * annotations (@Getter and @Setter) to generate boilerplate code for getters and setters.
 */
@Getter
@Setter
public class RisMeta {

  @XmlElement(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private DocumentType documentType;

  @XmlElement(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private Normgeber normgeber;

  @XmlElement(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private LocalDate entryIntoEffectDate;

  @XmlElement(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private LocalDate expiryDate;

  @XmlElementWrapper(name = "referenceNumbers", namespace = AdministrativeDirectiveLdml.RIS_NS)
  @XmlElement(name = "referenceNumber", namespace = AdministrativeDirectiveLdml.RIS_NS)
  private List<String> referenceNumbers;

  @XmlElementWrapper(name = "activeReferences", namespace = AdministrativeDirectiveLdml.RIS_NS)
  @XmlElement(name = "activeReference", namespace = AdministrativeDirectiveLdml.RIS_NS)
  private List<ActiveReference> activeReferences;

  @XmlElementWrapper(name = "fieldsOfLaw", namespace = AdministrativeDirectiveLdml.RIS_NS)
  @XmlElement(name = "fieldOfLaw", namespace = AdministrativeDirectiveLdml.RIS_NS)
  private List<FieldOfLaw> fieldsOfLaw;

  @XmlElementWrapper(name = "dateToQuoteList", namespace = AdministrativeDirectiveLdml.RIS_NS)
  @XmlElement(name = "dateToQuoteEntry", namespace = AdministrativeDirectiveLdml.RIS_NS)
  private List<LocalDate> dateToQuoteList;

  @XmlElementWrapper(
      name = "tableOfContentsEntries",
      namespace = AdministrativeDirectiveLdml.RIS_NS)
  @XmlElement(name = "tableOfContentsEntry", namespace = AdministrativeDirectiveLdml.RIS_NS)
  private List<String> tableOfContentsEntries;
}
