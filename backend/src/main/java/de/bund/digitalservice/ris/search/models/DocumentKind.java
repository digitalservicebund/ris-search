package de.bund.digitalservice.ris.search.models;

import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import java.util.Optional;
import lombok.Getter;

/** Enum for the different kinds of documents that can be searched for. */
@Getter
public enum DocumentKind {
  CASE_LAW("case-law"),
  LEGISLATION("norms"),
  LITERATURE("literature"),
  ADMINISTRATIVE_DIRECTIVE("administrative-directives"),
  ;

  private final String siteMapPath;

  DocumentKind(String siteMapPath) {
    this.siteMapPath = siteMapPath;
  }

  /**
   * Returns the id corresponding to the file name and document kinds provided.
   *
   * @param fileName the name of the file to extract the document id from
   * @param docKind the kind of document the file corresponds to
   * @return the id corresponding to the file name.
   */
  public static Optional<String> extractIdFromFileName(String fileName, DocumentKind docKind) {
    return switch (docKind) {
      case DocumentKind.ADMINISTRATIVE_DIRECTIVE, DocumentKind.LITERATURE ->
          Optional.ofNullable(fileName)
              .filter(e -> e.endsWith(".akn.xml"))
              .map(path -> path.substring(path.lastIndexOf("/") + 1, path.length() - 8));
      case DocumentKind.CASE_LAW ->
          Optional.ofNullable(fileName)
              .filter(e -> e.endsWith(".xml"))
              .map(path -> path.substring(path.lastIndexOf("/") + 1, path.length() - 4));
      case DocumentKind.LEGISLATION ->
          EliFile.fromString(fileName).map(EliFile::getExpressionEli).map(ExpressionEli::toString);
      case null -> Optional.empty();
    };
  }
}
