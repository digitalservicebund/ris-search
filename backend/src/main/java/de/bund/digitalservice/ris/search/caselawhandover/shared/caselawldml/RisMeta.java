package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents the RIS metadata for a legal case document. This class contains various fields that
 * describe the metadata attributes associated with a legal case, such as decision names, previous
 * and ensuing decisions, file numbers, document type, court information, legal forces, and more.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RisMeta {

  @XmlElementWrapper(name = "aktenzeichenListe", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<RisAktenzeichen> risAktenzeichen;

  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risDokumentTyp;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisGericht risGericht;

  @XmlElement(name = "rechtskraft", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risRechtskraft;

  @XmlElementWrapper(name = "sachgebiete", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "sachgebiet", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risSachgebiete;

  @XmlElementWrapper(name = "streitjahre", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "streitjahr", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risStreitjahre;

  @XmlElement(name = "spruchkoerper", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risSpruchkoerper;

  @XmlElementWrapper(name = "deviatingCourts", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "deviatingCourt", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> deviatingCourt;

  @XmlElementWrapper(name = "abweichendeDaten", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "abweichendesDatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risAbweichendeDaten;

  @XmlElementWrapper(name = "abweichendeDokumentnummern", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "abweichendeDokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risAbweichendeDokumentnummern;

  @XmlElementWrapper(name = "abweichendeEclis", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "abweichenderEcli", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risAbweichendeEclis;

  @XmlElement(name = "dokumentationsstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risDokumentationsstelle;

  /**
   * Returns a combined court keyword consisting of court type and court location. If court location
   * is not available, it returns only the court type.
   *
   * @return the combined court keyword
   */
  public String getCourtKeyword() {
    if (risGericht == null) return null;
    if (risGericht.getGerichtsort() == null) {
      return risGericht.getGerichtstyp();
    }
    return String.format("%s %s", risGericht.getGerichtstyp(), risGericht.getGerichtsort());
  }

  public List<String> getAktenzeichen() {
    return risAktenzeichen.stream()
        .filter(a -> "Aktenzeichen".equals(a.getDomainTerm()))
        .map(RisAktenzeichen::getValue)
        .toList();
  }
}
