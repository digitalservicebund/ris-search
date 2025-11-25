package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
