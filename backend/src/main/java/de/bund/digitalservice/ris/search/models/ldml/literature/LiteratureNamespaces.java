package de.bund.digitalservice.ris.search.models.ldml.literature;

/**
 * A utility class that provides namespace constants used in the context of literature metadata and
 * legal document modeling.
 *
 * <p>This class contains static constants representing XML namespaces for serialization and
 * deserialization processes in various literature-related domains. It is designed to provide a
 * centralized location for managing namespace URIs.
 *
 * <p>Constants: - AKN_NS: Represents the namespace URI for Akoma Ntoso (LegalDocumentML) version
 * 3.0. - RIS_SELBSTSTAENDIG_NS: Represents the namespace URI for metadata of "selbstständig"
 * (independent) literature publications in the RIS system. - RIS_UNSELBSTSTAENDIG_NS: Represents
 * the namespace URI for metadata of "unselbstständig" (dependent) literature publications in the
 * RIS system.
 *
 * <p>The constructor is private to prevent instantiation of this utility class.
 */
public class LiteratureNamespaces {

  public static final String AKN_NS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
  public static final String RIS_UNSELBSTSTAENDIG_NS =
      "http://ldml.neuris.de/literature/unselbstaendig/meta/";
  public static final String RIS_SELBSTSTAENDIG_NS =
      "http://ldml.neuris.de/literature/selbstaendig/meta/";

  private LiteratureNamespaces() {}
}
