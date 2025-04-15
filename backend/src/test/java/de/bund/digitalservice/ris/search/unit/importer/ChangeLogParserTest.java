package de.bund.digitalservice.ris.search.unit.importer;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import java.io.IOException;
import java.util.List;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

class ChangeLogParserTest {

  @Test
  void parseNormsChangeLog() throws IOException {
    String file =
        """
                {
                    "changed": ["eli/bund/bgbl/1950/s513/1977-12-22/5/deu/1977-12-22/regelungstext-1.xml"],
                    "deleted": ["eli/bund/bgbl/1950/s513/1986-05-15/18/deu/1986-05-15/regelungstext-1.xml"]
                }
                """;

    ObjectMapper mapper = new ObjectMapper();
    Changelog actual = mapper.readValue(file, Changelog.class);

    Assertions.assertEquals(
        Sets.newHashSet(
            List.of("eli/bund/bgbl/1950/s513/1977-12-22/5/deu/1977-12-22/regelungstext-1.xml")),
        actual.getChanged());
    Assertions.assertEquals(
        Sets.newHashSet(
            List.of("eli/bund/bgbl/1950/s513/1986-05-15/18/deu/1986-05-15/regelungstext-1.xml")),
        actual.getDeleted());
    Assertions.assertFalse(actual.isChangeAll());
  }
}
