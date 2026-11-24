package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.literature.Gliederung;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisKongress;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisOrganisation;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisPerson;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisRoleItem;
import de.bund.digitalservice.ris.search.models.ldml.literature.RisSachgebiet;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Getter;

/**
 * Represents the metadata for RIS (Legal Information System) documents, specifically for
 * unselbstständige (dependent) publications.
 *
 * <p>This class is used to deserialize XML data related to the metadata structure of such
 * publications. It relies on JAXB (Jakarta XML Binding) for mapping XML elements and their
 * attributes to Java object fields.
 *
 * <p>Fields: - yearsOfPublication: A list of publication years that are part of the RIS metadata.
 * Annotated with JAXB annotations to handle XML element wrapping and namespace mapping. -
 * gliederung: A detailed structure (of type Gliederung) that is part of the metadata, representing
 * a hierarchical breakdown of the document's structure.
 */
@Getter
public class RisMeta {
  @XmlElementWrapper(name = "kongresse", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "kongress", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisKongress> kongresse;

  @XmlElementWrapper(name = "organisationen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "organisation", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisOrganisation> organisationen;

  @XmlElementWrapper(name = "personen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "person", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisPerson> personen;

  @XmlElementWrapper(name = "dokumenttypen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "dokumenttyp", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> dokumenttypen;

  @XmlElementWrapper(name = "veroeffentlichungsJahre", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "veroeffentlichungsJahr", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> veroeffentlichungsJahre;

  @XmlElement(name = "gliederung", namespace = LiteratureNamespaces.RIS_NS)
  private Gliederung gliederung;

  @XmlElementWrapper(name = "verfasserListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "verfasser", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> verfasserListe;

  @XmlElementWrapper(name = "mitarbeiterListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "mitarbeiter", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> mitarbeiterListe;

  @XmlElementWrapper(name = "urheberListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "urheber", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> urheberListe;

  @XmlElementWrapper(name = "kongressvermerke", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "kongressvermerk", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> kongressvermerke;

  @XmlElementWrapper(name = "sachgebiete", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "sachgebiet", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisSachgebiet> sachgebiete;

  @XmlElementWrapper(name = "titelAspekte", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "titelAspekt", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> titelAspekte;
}
