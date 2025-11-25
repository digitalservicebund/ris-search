package de.bund.digitalservice.ris;

import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

/**
 * Utility class for loading XML test data files from the test resources folder as {@link String}s
 *
 * <p>The XML files are organized under {@code src/test/resources/data/LDML/} in subfolders:
 *
 * <ul>
 *   <li>{@code literature/} for {@link Literature}
 *   <li>{@code caselaw/} for {@link CaseLawDocumentationUnit}
 *   <li>{@code norm/} for {@link Norm}
 * </ul>
 */
public class LoadXmlUtils {

  private static final String TEST_DATA_FOLDER = "/data/LDML";
  private static final String CASELAW_FOLDER = TEST_DATA_FOLDER + "/caselaw/";
  private static final String NORM_FOLDER = TEST_DATA_FOLDER + "/norm/";
  private static final String LITERATURE_FOLDER = TEST_DATA_FOLDER + "/literature/";
  private static final String ADMINISTRATIVE_REGULATION_FOLDER =
      TEST_DATA_FOLDER + "/administrative-directive/";

  /**
   * Loads an XML test file from the appropriate folder under {@code src/test/resources/data/LDML/}
   * and returns its contents as a UTF-8 encoded string.
   *
   * <p>The folder is determined automatically based on the given class:
   *
   * @param clazz the model class determining which subfolder to load from
   * @param fileName the name of the XML file to load (e.g. {@code "example.xml"})
   * @return the XML file contents as a {@link String}
   * @throws RuntimeException if the file cannot be found or read
   */
  public static String loadXmlAsString(final Class<?> clazz, final String fileName) {
    try {
      return IOUtils.toString(
          Objects.requireNonNull(getResource(clazz, fileName).openStream()),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static URL getResource(final Class<?> clazz, final String fileName) {
    String folder;
    if (clazz == Literature.class) {
      folder = LITERATURE_FOLDER;
    } else if (clazz == CaseLawDocumentationUnit.class) {
      folder = CASELAW_FOLDER;
    } else if (clazz == Norm.class) {
      folder = NORM_FOLDER;
    } else if (clazz == AdministrativeDirective.class) {
      folder = ADMINISTRATIVE_REGULATION_FOLDER;
    } else {
      throw new IllegalArgumentException("Unsupported class type: " + clazz.getSimpleName());
    }
    return Optional.ofNullable(LoadXmlUtils.class.getResource(folder + fileName))
        .orElseThrow(
            () -> new RuntimeException("Could not find data " + fileName + " in " + folder));
  }
}
