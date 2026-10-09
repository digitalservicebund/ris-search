package de.bund.digitalservice.ris.search.utils;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

/** Context class for namespaces in norm xml. */
public class LegalDocMLDeContext implements NamespaceContext {
  public static final String AKN_SCHEMA = "http://rechtsinformationen.bund.de/schema/norm/0.1";
  public static final String RIS_SCHEMA =
      "http://rechtsinformationen.bund.de/schema/norm-metadata/0.1";

  @Override
  public String getNamespaceURI(String prefix) {
    if ("akn".equals(prefix)) {
      return AKN_SCHEMA;
    }
    if ("ris".equals(prefix)) {
      return RIS_SCHEMA;
    }
    return null;
  }

  @Override
  public String getPrefix(String namespaceURI) {
    if (AKN_SCHEMA.equals(namespaceURI)) {
      return "akn";
    }
    if (RIS_SCHEMA.equals(namespaceURI)) {
      return "ris";
    }
    return null;
  }

  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    return null;
  }
}
