package de.bund.digitalservice.ris.search.models.ldml.literature;

/**
 * A utility class that provides namespace constants used in the context of literature metadata and
 * legal document modeling.
 *
 * <p>Constants: - AKN_NS: Represents the namespace URI for Akoma Ntoso (LegalDocumentML) version
 * 3.0. - RIS_NS: Represents the namespace URI for RIS metadata of both selbstständige (independent)
 * and unselbstständige (dependent) literature publications.
 *
 * <p>The constructor is private to prevent instantiation of this utility class.
 */
public class LiteratureNamespaces {

  public static final String AKN_NS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
  public static final String RIS_NS = "http://rechtsinformationen.bund.de/schema/ris/0.1";

  private LiteratureNamespaces() {}
}
