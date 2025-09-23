package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EliUtilsTest {

  @Test
  void testWorkEliParsesStringCorrectly() {
    String eliFileString =
        "eli/bund/bgbl-1/2021/1234/2021-01-01/1/deu/2021-01-20/regelungstext-1.xml";
    WorkEli workEli = EliFile.fromString(eliFileString).get().getWorkEli();
    assertEquals("bund", workEli.jurisdiction());
    assertEquals("bgbl-1", workEli.agent());
    assertEquals("2021", workEli.year());
    assertEquals("1234", workEli.naturalIdentifier());
    assertEquals("eli/bund/bgbl-1/2021/1234", workEli.toString());
  }

  @Test
  void testExpressionEliParsesStringCorrectly() {
    var eliFileString = "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/1964-01-01/regelungstext-1.xml";
    ExpressionEli expressionEli = EliFile.fromString(eliFileString).get().getExpressionEli();
    assertEquals("bund", expressionEli.jurisdiction());
    assertEquals("bgbl-1", expressionEli.agent());
    assertEquals("1950", expressionEli.year());
    assertEquals("s69", expressionEli.naturalIdentifier());
    assertEquals("1964-01-01", expressionEli.pointInTime().toString());
    assertEquals(1, expressionEli.version());
    assertEquals("deu", expressionEli.language());
    assertEquals("eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu", expressionEli.toString());
  }

  @Test
  void testManifestationEliParsesStringCorrectly() {
    String eliFile = "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/1964-02-02/regelungstext-1.xml";
    ManifestationEli manifestationEli = EliFile.fromString(eliFile).get().getManifestationEli();
    assertEquals("bund", manifestationEli.jurisdiction());
    assertEquals("bgbl-1", manifestationEli.agent());
    assertEquals("1950", manifestationEli.year());
    assertEquals("s69", manifestationEli.naturalIdentifier());
    assertEquals("1964-01-01", manifestationEli.pointInTime().toString());
    assertEquals(1, manifestationEli.version());
    assertEquals("deu", manifestationEli.language());
    assertEquals("1964-02-02", manifestationEli.pointInTimeManifestation().toString());
    assertEquals("regelungstext-1", manifestationEli.subtype());
    assertEquals(
        "eli/bund/bgbl-1/1950/s69/1964-01-01/1/deu/1964-02-02/regelungstext-1.xml",
        manifestationEli.toString());
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
