package de.bund.digitalservice.ris.search.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/** Enum for the different types of documents that can be searched for. */
@Getter
public enum DocumentKind {
  CASELAW("R"),
  LEGISLATION("N"),
  LITERATURE("L");

  private final String value;

  DocumentKind(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public static DocumentKind extendedValueOf(String value) {
    return switch (value.toUpperCase()) {
      case "R" -> DocumentKind.CASELAW;
      case "N" -> DocumentKind.LEGISLATION;
      case "L" -> DocumentKind.LITERATURE;
      default -> DocumentKind.valueOf(value.toUpperCase());
    };
  }

  /**
   * Using the {@link Component} annotation, this converter class is registered to handle extended
   * value conversion when this enum is used as a request parameter.
   */
  @Component
  public static class FromStringConverter implements Converter<String, DocumentKind> {

    @Override
    public DocumentKind convert(@NotNull String source) {
      return DocumentKind.extendedValueOf(source);
    }
  }
}
