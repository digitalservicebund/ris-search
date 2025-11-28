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
  LITERATURE("L"),
  ADMINISTRATIVE_DIRECTIVE("V");

  private final String value;

  DocumentKind(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Resolves a string value to the corresponding {@code DocumentKind} enum constant. This method
   * extends the default {@code valueOf} behavior by mapping specific character codes ('R', 'N',
   * 'L') to predefined constants, while still allowing standard enum lookup for other valid names.
   *
   * @param value the string representation of the desired {@code DocumentKind}. It is
   *     case-insensitive and can be one of the special codes ('R', 'N', 'L') or a name of the enum
   *     constant.
   * @return the {@code DocumentKind} constant corresponding to the input value. If the value
   *     matches 'R', 'N', or 'L', the method returns the respective predefined constant (e.g.,
   *     {@code DocumentKind.CASELAW} for 'R'). For other input values, it defaults to {@code
   *     DocumentKind.valueOf}.
   * @throws IllegalArgumentException if the input value does not correspond to any defined {@code
   *     DocumentKind} constant.
   * @throws NullPointerException if the input value is null.
   */
  public static DocumentKind extendedValueOf(String value) {
    return switch (value.toUpperCase()) {
      case "R" -> DocumentKind.CASELAW;
      case "N" -> DocumentKind.LEGISLATION;
      case "L" -> DocumentKind.LITERATURE;
      case "V" -> DocumentKind.ADMINISTRATIVE_DIRECTIVE;
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
