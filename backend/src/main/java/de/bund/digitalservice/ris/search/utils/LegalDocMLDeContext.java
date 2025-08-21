package de.bund.digitalservice.ris.search.utils;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

class LegalDocMLDeContext implements NamespaceContext {
  @Override
  public String getNamespaceURI(String prefix) {
    if ("akn".equals(prefix)) {
      return "http://Inhaltsdaten.LegalDocML.de/1.8.2/";
    }
    if ("ris".equals(prefix)) {
      return "http://MetadatenRIS.LegalDocML.de/1.8.2/";
    }
    return null;
  }

  @Override
  public String getPrefix(String namespaceURI) {
    if ("http://Inhaltsdaten.LegalDocML.de/1.8.2/".equals(namespaceURI)) {
      return "akn";
    }
    if ("http://MetadatenRIS.LegalDocML.de/1.8.2/".equals(namespaceURI)) {
      return "ris";
    }
    return null;
  }

  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    return null;
  }
}
