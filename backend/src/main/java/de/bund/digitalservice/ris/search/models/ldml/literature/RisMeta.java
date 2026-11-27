package de.bund.digitalservice.ris.search.models.ldml.literature;

import de.bund.digitalservice.ris.search.models.ldml.ReferenzNorm;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Getter;

/**
 * Represents the RIS metadata ({@code ris:meta}) of literature documents. Used for both
 * selbstständige (SLI) and unselbstständige (ULI) literature, which share one schema.
 *
 * <p>Not every element is present for every literature type: some elements (e.g. ausgabe,
 * teilbaende, verlagsangaben, herausgeber*) are only used by selbstständige literature. Absent
 * elements are deserialized as {@code null}.
 *
 * <p>This class relies on JAXB (Jakarta XML Binding) for mapping XML elements and their attributes
 * to Java object fields.
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

  @XmlElementWrapper(name = "gesamtfussnoten", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "gesamtfussnote", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> gesamtfussnoten;

  @XmlElementWrapper(name = "veroeffentlichungsJahre", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "veroeffentlichungsJahr", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> veroeffentlichungsJahre;

  @XmlElement(name = "gliederung", namespace = LiteratureNamespaces.RIS_NS)
  private Gliederung gliederung;

  @XmlElementWrapper(name = "titelkurzformen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "titelkurzform", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> titelKurzformen;

  @XmlElementWrapper(name = "sonstigerSachtitelListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "sonstigerSachtitel", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> sonstigeSachtitel;

  @XmlElementWrapper(name = "gesamttitelangaben", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "gesamttitel", namespace = LiteratureNamespaces.RIS_NS)
  private List<Gesamttitel> gesamttitelangaben;

  @XmlElement(namespace = LiteratureNamespaces.RIS_NS)
  private String ausgabe;

  @XmlElementWrapper(name = "teilbaende", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "teilband", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> teilbaende;

  @XmlElementWrapper(name = "verfasserListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "verfasser", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> verfasserListe;

  @XmlElementWrapper(name = "mitarbeiterListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "mitarbeiter", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> mitarbeiterListe;

  @XmlElementWrapper(name = "bearbeiterListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "bearbeiter", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> bearbeiterListe;

  @XmlElementWrapper(name = "begruenderListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "begruender", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> begruenderListe;

  @XmlElementWrapper(name = "herausgeberPersonen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "herausgeberPerson", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> herausgeberPersonen;

  @XmlElementWrapper(name = "herausgeberInstitutionen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "herausgeberInstitution", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> herausgeberInstitutionen;

  @XmlElementWrapper(name = "urheberListe", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "urheber", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> urheberListe;

  @XmlElementWrapper(name = "verlagsangaben", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "verlagsangabe", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> verlagsangaben;

  @XmlElementWrapper(name = "hochschulschriftenvermerke", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "hochschulschriftenvermerk", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> hochschulschriftenvermerke;

  @XmlElementWrapper(name = "kongressvermerke", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "kongressvermerk", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> kongressvermerke;

  @XmlElementWrapper(name = "sachgebiete", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "sachgebiet", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisSachgebiet> sachgebiete;

  @XmlElementWrapper(name = "titelAspekte", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "titelAspekt", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> titelAspekte;

  @XmlElementWrapper(
      name = "bibliothekarischeVerweisungen",
      namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "bibliothekarischeVerweisung", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> bibliothekarischeVerweisungen;

  @XmlElement(namespace = LiteratureNamespaces.RIS_NS)
  private String kollation;

  @XmlElementWrapper(name = "vorauflagen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "vorauflage", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> vorauflagen;

  @XmlElementWrapper(name = "regionen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "region", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> regionen;

  @XmlElementWrapper(name = "referenzenNorm", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "referenzNorm", namespace = LiteratureNamespaces.RIS_NS)
  private List<ReferenzNorm> referenzenNorm;

  @XmlElementWrapper(name = "berufsbilder", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "berufsbild", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> berufsbilder;

  @XmlElementWrapper(name = "definitionen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "definition", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisDefinition> definitionen;

  @XmlElementWrapper(name = "kuendigungsgruende", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "kuendigungsgrund", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> kuendigungsgruende;

  @XmlElementWrapper(name = "tarifvertraege", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "tarifvertrag", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> tarifvertraege;

  @XmlElementWrapper(name = "personenInstitutionen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "personInstitution", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisPersonInstitution> personenInstitutionen;

  @XmlElementWrapper(name = "ueberPersonenInstitutionen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "ueberPersonInstitution", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> ueberPersonenInstitutionen;

  @XmlElementWrapper(name = "fuerPersonenInstitutionen", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "fuerPersonInstitution", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> fuerPersonenInstitutionen;

  @XmlElementWrapper(name = "ueberkongresse", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "ueberkongress", namespace = LiteratureNamespaces.RIS_NS)
  private List<RisRoleItem> ueberKongresse;

  @XmlElementWrapper(name = "abweichendeDokumentnummern", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "abweichendeDokumentnummer", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> abweichendeDokumentnummern;
}
