package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FrbrElement {

  @XmlElement(name = "FRBRthis", namespace = CaseLawLdml.AKN_NS)
  private FrbrThis frbrThis;

  @XmlElement(name = "FRBRuri", namespace = CaseLawLdml.AKN_NS)
  private FrbrUri frbrUri;

  @XmlElement(name = "FRBRalias", namespace = CaseLawLdml.AKN_NS)
  private List<FrbrAlias> frbrAlias;

  @XmlElement(name = "FRBRdate", namespace = CaseLawLdml.AKN_NS)
  private FrbrDate frbrDate;

  @XmlElement(name = "FRBRauthor", namespace = CaseLawLdml.AKN_NS)
  private FrbrAuthor frbrAuthor;

  @XmlElement(name = "FRBRcountry", namespace = CaseLawLdml.AKN_NS)
  private FrbrCountry frbrCountry;

  @XmlElement(name = "FRBRlanguage", namespace = CaseLawLdml.AKN_NS)
  private FrbrLanguage frbrLanguage;

  private String getAliasValueByName(String aliasName) {
    return frbrAlias.stream()
        .filter(alias -> alias.getName().equals(aliasName))
        .findFirst()
        .map(FrbrAlias::getValue)
        .orElse(null);
  }

  public FrbrElement withFrbrThisAndUri(String value) {
    this.frbrThis = new FrbrThis(value);
    this.frbrUri = new FrbrUri(value);
    return this;
  }

  public String getEcliAliasValue() {
    return getAliasValueByName("ecli");
  }

  public String getUuidAliasValue() {
    return getAliasValueByName("uebergreifende-id");
  }
}
