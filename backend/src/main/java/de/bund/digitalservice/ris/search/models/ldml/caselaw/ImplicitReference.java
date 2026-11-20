package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an implicit reference within the context of a case law, providing details of how the
 * reference is displayed and linked to norm and caselaw data.
 */
@Getter
@Setter
public class ImplicitReference {
  private static final String FUNDSTELLENTYP_AMTLICH = "amtlich";
  private static final String FUNDSTELLENTYP_NICHTAMTLICH = "nichtamtlich";

  @XmlAttribute(name = "domainTerm")
  private String domainTerm;

  @XmlElement(name = "vorgehendeEntscheidung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private VorgehendeEntscheidung vorgehendeEntscheidung;

  @XmlElement(name = "nachgehendeEntscheidung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private NachgehendeEntscheidung nachgehendeEntscheidung;

  @XmlElement(name = "referenzUnselbststaendigeLiteratur", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private ReferenzUnselbststaendigeLiteratur referenzUnselbststaendigeLiteratur;

  @XmlElement(name = "referenzSelbststaendigeLiteratur", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private ReferenzSelbststaendigeLiteratur referenzSelbststaendigeLiteratur;

  @XmlElement(name = "referenzRechtsprechung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private ReferenzRechtsprechung referenzRechtsprechung;

  @XmlElement(name = "referenzVerwaltungsvorschrift", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private ReferenzVerwaltungsvorschrift referenzVerwaltungsvorschrift;

  @XmlElement(name = "fundstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Fundstelle fundstelle;

  @XmlElement(name = "referenzNorm", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private ReferenzNorm referenzNorm;

  /**
   * Returns the formatted amtliche Fundstelle (official citation) of this document, but only if it
   * is flagged as official ("amtlich").
   *
   * @return the formatted citation, or {@code null} if absent or not an official citation
   */
  public String getAmtlicheFundstelleFormatted() {
    return getFundstelleFormattedByTyp(FUNDSTELLENTYP_AMTLICH);
  }

  /**
   * Returns the formatted nichtamtliche Fundstelle (non-official citation) of this document, but
   * only if it is flagged as non-official ("nichtamtlich").
   *
   * @return the formatted citation, or {@code null} if absent or not a non-official citation
   */
  public String getNichtamtlicheFundstelleFormatted() {
    return getFundstelleFormattedByTyp(FUNDSTELLENTYP_NICHTAMTLICH);
  }

  private String getFundstelleFormattedByTyp(String fundstellenTyp) {
    if (fundstelle == null || !fundstellenTyp.equalsIgnoreCase(fundstelle.getFundstellenTyp())) {
      return null;
    }
    return fundstelle.getFormatted();
  }

  /**
   * Returns the formatted Gesetzeskraft (binding legal force) ruling of every Einzelnorm cited
   * within this norm reference.
   *
   * @return the formatted rulings, or an empty list if none are present
   */
  public List<String> getGesetzeskraftFormattedList() {
    if (referenzNorm == null || referenzNorm.getEinzelnorm() == null) {
      return List.of();
    }
    return referenzNorm.getEinzelnorm().stream()
        .map(Einzelnorm::getGesetzeskraft)
        .filter(Objects::nonNull)
        .map(Gesetzeskraft::getFormatted)
        .filter(Objects::nonNull)
        .toList();
  }

  /**
   * Returns the formatted Normenkette entries for this norm reference, i.e. the law's abbreviation
   * combined with each cited provision's designation (e.g. "BGB § 823"). If no provisions are
   * cited, the abbreviation alone is returned as a single entry.
   *
   * @return the formatted Normenkette entries, or an empty list if no norm reference is present
   */
  public List<String> getNormenketteFormattedList() {
    if (referenzNorm == null) {
      return List.of();
    }
    String abkuerzung = referenzNorm.getAbkuerzung();
    List<Einzelnorm> einzelnormen = referenzNorm.getEinzelnorm();
    if (einzelnormen == null || einzelnormen.isEmpty()) {
      return abkuerzung == null || abkuerzung.isBlank() ? List.of() : List.of(abkuerzung.trim());
    }
    return einzelnormen.stream()
        .map(einzelnorm -> formatNormenketteEntry(abkuerzung, einzelnorm.getBezeichnung()))
        .filter(Objects::nonNull)
        .toList();
  }

  private static String formatNormenketteEntry(String abkuerzung, String bezeichnung) {
    String trimmedAbkuerzung = abkuerzung != null ? abkuerzung.trim() : null;
    String trimmedBezeichnung = bezeichnung != null ? bezeichnung.trim() : null;
    if (trimmedAbkuerzung == null || trimmedAbkuerzung.isEmpty()) {
      return trimmedBezeichnung;
    }
    if (trimmedBezeichnung == null || trimmedBezeichnung.isEmpty()) {
      return trimmedAbkuerzung;
    }
    return trimmedAbkuerzung + " " + trimmedBezeichnung;
  }
}
