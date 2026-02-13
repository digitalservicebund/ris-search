package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
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
  private FrbrDate frbrDate;

  @XmlElement(name = "FRBRauthor", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrAuthor frbrAuthor;

  @XmlElement(name = "FRBRcountry", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrCountry frbrCountry;

  @XmlElement(name = "FRBRlanguage", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrLanguage frbrLanguage;

  private String getAliasValueByName(String aliasName) {
    return frbrAlias.stream()
        .filter(alias -> alias.getName().equals(aliasName))
        .findFirst()
        .map(FrbrAlias::getValue)
        .orElse(null);
  }

  /**
   * Convenience method to set both FRBRthis and FRBRuri to the same value
   *
   * @param value the value to set for both FRBRthis and FRBRuri
   * @return the updated FrbrElement instance
   */
  public FrbrElement withFrbrThisAndUri(String value) {
    this.frbrThis = new FrbrThis(value);
    this.frbrUri = new FrbrUri(value);
    return this;
  }

  public String getEcliAliasValue() {
    return getAliasValueByName("ECLI");
  }

  public String getUuidAliasValue() {
    return getAliasValueByName("Übergreifende ID");
  }
}
