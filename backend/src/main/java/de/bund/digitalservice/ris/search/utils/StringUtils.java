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
}
