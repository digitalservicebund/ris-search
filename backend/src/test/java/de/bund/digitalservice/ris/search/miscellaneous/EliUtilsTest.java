package de.bund.digitalservice.ris.search.miscellaneous;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEliPath;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEliPath;
import de.bund.digitalservice.ris.search.utils.eli.WorkEliPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EliUtilsTest {

  @Test
  void testWorkEliParsesStringCorrectly() {
    String eliFileString =
        "eli/bund/bgbl-1/2021/1234/2021-01-01/1/deu/2021-01-20/regelungstext-1.xml";
    WorkEliPath workEli = EliFile.fromString(eliFileString).get().getWorkEliPath();
    assertEquals("bund", workEli.jurisdiction());
    assertEquals("bgbl-1", workEli.agent());
    assertEquals("2021", workEli.year());
    assertEquals("1234", workEli.naturalIdentifier());
    assertEquals("eli/bund/bgbl-1/2021/1234", workEli.toString());
  }

  @Test
  void testExpressionEliParsesStringCorrectly() {
    var eliFileString = "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/1964-01-01/regelungstext-1.xml";
    ExpressionEliPath expressionEliPath =
        EliFile.fromString(eliFileString).get().getExpressionEliPath();
    assertEquals("bund", expressionEliPath.jurisdiction());
    assertEquals("bgbl-1", expressionEliPath.agent());
    assertEquals("1950", expressionEliPath.year());
    assertEquals("s69", expressionEliPath.naturalIdentifier());
    assertEquals("1964-01-01", expressionEliPath.pointInTime().toString());
    assertEquals(1, expressionEliPath.version());
    assertEquals("deu", expressionEliPath.language());
    assertEquals("eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu", expressionEliPath.toString());
  }

  @Test
  void testManifestationEliParsesStringCorrectly() {
    String eliFile = "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/1964-02-02/regelungstext-1.xml";
    ManifestationEliPath manifestationEliPath =
        EliFile.fromString(eliFile).get().getManifestationEliPath();
    assertEquals("bund", manifestationEliPath.jurisdiction());
    assertEquals("bgbl-1", manifestationEliPath.agent());
    assertEquals("1950", manifestationEliPath.year());
    assertEquals("s69", manifestationEliPath.naturalIdentifier());
    assertEquals("1964-01-01", manifestationEliPath.pointInTime().toString());
    assertEquals(1, manifestationEliPath.version());
    assertEquals("deu", manifestationEliPath.language());
    assertEquals("1964-02-02", manifestationEliPath.pointInTimeManifestation().toString());
    assertEquals("regelungstext-1", manifestationEliPath.subtype());
    assertEquals(
        "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/1964-02-02/regelungstext-1.xml",
        manifestationEliPath.toString());
  }

  @ValueSource(
      strings = {
        "",
        "eli/bund",
        "eli/bund/bgbl-1/2021/1234",
        "eli/bund/bgbl-1//2021/1234/regelungstext-1",
        "eli/bund/bgbl-1/1950/s69/1964-01-01/a/deu",
        "eli/bund/bgbl-1/1950/s69/1964-1-1/1/deu",
        "eli/bund/bgbl-1/1950/s69/1964-01-01/a/deu/1964-2-2/regelungstext-1.xml",
        "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/regelungstext"
      })
  @ParameterizedTest
  void testInvalidEliFile(String eliFile) {
    assertTrue(EliFile.fromString(eliFile).isEmpty());
  }
}
