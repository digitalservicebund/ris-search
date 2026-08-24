package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import de.bund.digitalservice.ris.search.models.ldml.FrbrAuthor;
import de.bund.digitalservice.ris.search.models.ldml.FrbrLanguage;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the FRBR element in the case law LDML format. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FrbrElement {

  @XmlElement(name = "FRBRthis", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrThis frbrThis;

  @XmlElement(name = "FRBRuri", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrUri frbrUri;

  @XmlElement(name = "FRBRalias", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<FrbrAlias> frbrAlias;

  @XmlElement(name = "FRBRdate", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<FrbrDate> frbrDates;

  @XmlElement(name = "FRBRauthor", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrAuthor frbrAuthor;

  @XmlElement(name = "FRBRcountry", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrCountry frbrCountry;

  @XmlElement(name = "FRBRlanguage", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrLanguage frbrLanguage;

  private String getAliasValueByName(String aliasName) {
    if (frbrAlias == null) {
      return null;
    }
    return frbrAlias.stream()
        .filter(alias -> alias.getName().equalsIgnoreCase(aliasName))
        .findFirst()
        .map(FrbrAlias::getValue)
        .orElse(null);
  }

  public String getEcliAliasValue() {
    return getAliasValueByName("ecli");
  }

  public String getAktenzeichenAliasValue() {
    return getAliasValueByName("Aktenzeichen");
  }

  public String getCelexAliasValue() {
    return getAliasValueByName("CELEX");
  }

  /**
   * Returns the value of this element's single FRBRdate, which represents the document's decision
   * date regardless of its {@code name} attribute (e.g. "Entscheidungsdatum", "mitteilungsdatum",
   * "datumDerZustellungAnVerkuendungsStatt").
   *
   * @return the decision date value, or {@code null} if no FRBRdate is present
   */
  public String getEntscheidungsdatumValue() {
    if (frbrDates == null || frbrDates.isEmpty()) {
      return null;
    }
    return frbrDates.get(0).getDate();
  }

  private Optional<FrbrDate> findDateByName(String name) {
    return frbrDates.stream().filter(d -> name.equalsIgnoreCase(d.getName())).findFirst();
  }

  /**
   * Looks up a FRBRdate's value by its {@code name} attribute.
   *
   * @param name the name attribute of the date to look up
   * @return the date value, or {@code null} if not present
   */
  public String getDateByName(String name) {
    if (frbrDates == null) {
      return null;
    }
    return findDateByName(name).map(FrbrDate::getDate).orElse(null);
  }

  public String getErstveroeffentlichungValue() {
    return getDateByName("erstveroeffentlichung");
  }

  public String getLetzteVeroeffentlichungValue() {
    return getDateByName("letzteVeroeffentlichung");
  }

  public String getMitteilungsdatumValue() {
    return getDateByName("mitteilungsdatum");
  }
}
