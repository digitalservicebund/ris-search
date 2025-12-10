package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureType;

/** Maps a LiteratureType to its api representation */
public class LiteratureTypeMapper {
  private LiteratureTypeMapper() {}

  /**
   * Maps a LiteratureType to its api representation
   *
   * @param documentNumber String the type is determined by its document number
   * @return Api representation of a literature type
   */
  public static String mapLiteratureType(String documentNumber) {
    return switch (LiteratureType.getByDocumentNumber(documentNumber)) {
      case SLI -> "sli";
      case ULI -> "uli";
      default -> {
        String msg = String.format("Unknown documentType for document Number %s", documentNumber);
        throw new IllegalStateException(msg);
      }
    };
  }
}
