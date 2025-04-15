package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EliUtilsTest {

  @Test
  void testWorkEliParsesStringCorrectly() {
    String workEliString = "eli/bund/bgbl-1/2021/1234/regelungstext-1";
    WorkEli workEli = WorkEli.fromString(workEliString);
    assertEquals("bund", workEli.jurisdiction());
    assertEquals("bgbl-1", workEli.agent());
    assertEquals("2021", workEli.year());
    assertEquals("1234", workEli.naturalIdentifier());
    assertEquals("regelungstext-1", workEli.subtype());
    assertEquals(workEliString, workEli.toString());
  }

  @ValueSource(
      strings = {
        "eli/bund/bgbl-1//2021/1234/regelungstext-1",
        "eli/bund/bgbl-1/2021/1234",
        "",
        "eli/bund"
      })
  @ParameterizedTest
  void testWorkEliThrowsErrorOnInvalidEli(String workEliString) {
    assertThrows(Exception.class, () -> WorkEli.fromString(workEliString));
  }

  @Test
  void testExpressionEliParsesStringCorrectly() {
    var expressionEliString = "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/regelungstext-1";
    ExpressionEli expressionEli = ExpressionEli.fromString(expressionEliString);
    assertEquals("bund", expressionEli.jurisdiction());
    assertEquals("bgbl-1", expressionEli.agent());
    assertEquals("1950", expressionEli.year());
    assertEquals("s69", expressionEli.naturalIdentifier());
    assertEquals("1964-01-01", expressionEli.pointInTime().toString());
    assertEquals(1, expressionEli.version());
    assertEquals("deu", expressionEli.language());
    assertEquals("regelungstext-1", expressionEli.subtype());
    assertEquals(expressionEliString, expressionEli.toString());

    assertEquals("eli/bund/bgbl-1/1950/s69/regelungstext-1", expressionEli.getWorkEli().toString());
  }

  @ValueSource(
      strings = {
        "eli/bund/bgbl-1/1950/s69/1964-01-01/a/deu",
        "",
        "eli/bund/bgbl-1/1950/s69/1964-1-1/1/deu"
      })
  @ParameterizedTest
  void testExpressionEliThrowsErrorOnInvalidEli(String expressionEliString) {

    assertThrows(Exception.class, () -> ExpressionEli.fromString(expressionEliString));
  }

  @Test
  void testManifestationEliParsesStringCorrectly() {
    var manifestationEliString =
        "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/1964-02-02/regelungstext-1.xml";
    ManifestationEli manifestationEli = ManifestationEli.fromString(manifestationEliString).get();
    assertEquals("bund", manifestationEli.jurisdiction());
    assertEquals("bgbl-1", manifestationEli.agent());
    assertEquals("1950", manifestationEli.year());
    assertEquals("s69", manifestationEli.naturalIdentifier());
    assertEquals("1964-01-01", manifestationEli.pointInTime().toString());
    assertEquals(1, manifestationEli.version());
    assertEquals("deu", manifestationEli.language());
    assertEquals("1964-02-02", manifestationEli.pointInTimeManifestation().toString());
    assertEquals("regelungstext-1", manifestationEli.subtype());
    assertEquals(manifestationEliString, manifestationEli.toString());

    assertEquals(
        "eli/bund/bgbl-1/1950/s69/regelungstext-1", manifestationEli.getWorkEli().toString());
  }

  @ValueSource(
      strings = {
        "eli/bund/bgbl-1/1950/s69/1964-01-01/a/deu/1964-2-2/regelungstext-1.xml",
        "",
        "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/regelungstext"
      })
  @ParameterizedTest
  void testManifestationEliThrowsErrorOnInvalidEli(String expressionEliString) {

    assertThrows(Exception.class, () -> ManifestationEli.fromString(expressionEliString).get());
  }
}
