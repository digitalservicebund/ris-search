package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureType;

public class LiteratureTypeMapper {
  private LiteratureTypeMapper() {}

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
