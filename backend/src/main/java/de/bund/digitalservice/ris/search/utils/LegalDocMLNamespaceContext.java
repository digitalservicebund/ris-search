package de.bund.digitalservice.ris.search.utils;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

abstract class LegalDocMLNamespaceContext implements NamespaceContext {

  private static final String AKN_NAMESPACE_PREFIX = "akn";
  private static final String RIS_NAMESPACE_PREFIX = "ris";

  private final String aknNamespaceUrl;
  private final String risNamespaceUrl;

  protected LegalDocMLNamespaceContext(String aknNamespaceUrl, String risNamespaceUrl) {
    super();
    this.aknNamespaceUrl = aknNamespaceUrl;
    this.risNamespaceUrl = risNamespaceUrl;
  }

  @Override
  public String getNamespaceURI(String prefix) {
    return switch (prefix) {
      case AKN_NAMESPACE_PREFIX -> this.aknNamespaceUrl;
      case RIS_NAMESPACE_PREFIX -> this.risNamespaceUrl;
      default -> null;
    };
  }

  @Override
  public String getPrefix(String namespaceURI) {
    if (this.aknNamespaceUrl.equals(namespaceURI)) {
      return AKN_NAMESPACE_PREFIX;
    }
    if (this.risNamespaceUrl.equals(namespaceURI)) {
      return RIS_NAMESPACE_PREFIX;
    }
    return null;
  }

  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    return null;
  }
}
