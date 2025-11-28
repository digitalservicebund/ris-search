package de.bund.digitalservice.ris.search.utils;

import jakarta.xml.bind.ValidationException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Utility class providing methods for string sanitization, null-safety checks, list handling,
 * validation, and text cleaning. This class is designed to simplify and streamline common
 * operations in the application.
 */
public class MappingUtils {

  private MappingUtils() {}

  /**
   * Sanitizes the provided HTML string by removing specific elements and returning plain text. The
   * method parses the HTML content, removes all elements that match the selector
   * "hcontainer[name=randnummer] > num", and extracts the remaining text content.
   *
   * @param htmlData the input string containing HTML; can be empty or null
   * @return a sanitized plain text representation of the input string; returns an empty string if
   *     the input is null or empty
   */
  public static String sanitizeHtmlFromString(String htmlData) {
    if (StringUtils.isNotEmpty(htmlData)) {
      Document document = Jsoup.parse(htmlData);
      Elements selectors = document.select("hcontainer[name=randnummer] > num");
      selectors.remove();
      return document.text();
    }
    return StringUtils.EMPTY;
  }

  /**
   * Safely applies the provided function to an input object, returning the result of the function
   * if the input is not null. If the input is null, this method returns null instead of applying
   * the function.
   *
   * @param <T> the type of the input object
   * @param <R> the type of the result produced by the function
   * @param input the input object to be processed; can be null
   * @param call the function to apply to the input object; must not be null
   * @return the result of applying the function to the input object, or null if the input object is
   *     null
   */
  public static <T, R> R nullSafeGet(T input, Function<? super T, R> call) {
    if (input == null) {
      return null;
    }
    return call.apply(input);
  }

  public static void validateNotNull(Object o, String message) throws ValidationException {
    validate(o != null, message);
  }

  /**
   * Validates a condition and throws a ValidationException if the condition is not met.
   *
   * @param test the condition to validate; if false, an exception will be thrown
   * @param message the message to include in the thrown ValidationException
   * @throws ValidationException if the test condition evaluates to false
   */
  public static void validate(boolean test, String message) throws ValidationException {
    if (!test) {
      throw new ValidationException(message);
    }
  }

  /**
   * Applies the given {@code Consumer} to the provided list if the list is not null and not empty.
   * This method ensures that the provided action is only executed when the list contains elements.
   *
   * @param <T> the type of elements in the list
   * @param collection the list to be checked and processed; can be null
   * @param call the {@code Consumer} to apply to the list if it is not empty
   */
  public static <T> void applyIfNotEmpty(List<T> collection, Consumer<List<T>> call) {
    if (collection != null && !collection.isEmpty()) {
      call.accept(collection);
    }
  }

  /**
   * Cleans the given text by replacing multiple whitespace characters with a single space and
   * trimming leading and trailing whitespace.
   *
   * @param text the input text to clean; can be null
   * @return a cleaned version of the input text; if the input is null, an empty string is returned
   */
  public static String cleanText(String text) {
    if (text == null) {
      return "";
    }
    return text.replaceAll("\\s+", " ").trim();
  }
}
