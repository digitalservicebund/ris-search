package de.bund.digitalservice.ris.search.utils;

/** Class for string utility functions. */
public class StringUtils {

  private StringUtils() {}

  /**
   * Removes the given prefix from the input if the input starts with the prefix. Otherwise, the
   * input is returned unchanged.
   *
   * @param input with a prefix that should be removed
   * @param prefix that should be removed from the input
   * @return input with the prefix removed
   */
  public static String stripPrefix(String input, String prefix) {
    if (input != null && prefix != null && input.startsWith(prefix)) {
      return input.substring(prefix.length());
    }

    return input;
  }

  /**
   * Converts the HTML markup used in parsed XSD documentation ({@code <br>}, {@code <ul>/<li>})
   * into plain text with equivalent line breaks and dashes, then strips any remaining tags.
   *
   * @param input the HTML-flavored text to convert
   * @return the plain-text equivalent, or the input unchanged if it is {@code null}
   */
  public static String stripHtml(String input) {
    if (input == null) {
      return null;
    }

    return input
        .replaceAll("(?i)<br\\s*/?>", "\n")
        .replaceAll("(?i)<li>", "- ")
        .replaceAll("(?i)</li>", "\n")
        .replaceAll("(?i)</?ul>", "")
        .replaceAll("(?i)<[^>]+>", "")
        .strip();
  }
}
