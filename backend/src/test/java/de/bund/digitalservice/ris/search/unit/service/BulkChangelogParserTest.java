package de.bund.digitalservice.ris.search.unit.service;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.service.BulkChangelogParser;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BulkChangelogParserTest {

  @Test
  void itDetectsChangeAllChangelogs() {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file")));
    Changelog log2 = new Changelog();
    log2.setChangeAll(true);
    Changelog log3 = new Changelog();
    log3.setDeleted(new HashSet<>(List.of("deleted")));

    Assertions.assertTrue(BulkChangelogParser.containsChangeAll(List.of(log1, log2, log3)));
  }

  @Test
  void itDetectsMissingChangeAllInChangelogs() {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file")));
    Changelog log3 = new Changelog();
    log3.setDeleted(new HashSet<>(List.of("deleted")));

    Assertions.assertFalse(BulkChangelogParser.containsChangeAll(List.of(log1, log3)));
  }

  @Test
  void itMergesAListOfConsecutiveChangelogsIntoASingleOne() {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("obsolete", "changed")));
    log1.setDeleted(new HashSet<>(List.of("obsolete")));
    Changelog log2 = new Changelog();
    log2.setChanged(new HashSet<>(List.of("changed2", "obsolete2")));
    log2.setDeleted(new HashSet<>(List.of("deleted")));
    Changelog log3 = new Changelog();
    log3.setChanged(new HashSet<>(List.of("changed3")));
    log3.setDeleted(new HashSet<>(List.of("obsolete2", "deleted2")));

    var result = BulkChangelogParser.mergeChangelogs(List.of(log1, log2, log3));

    Assertions.assertEquals(3, result.getChanged().size());
    Assertions.assertTrue(result.getChanged().contains("changed"));
    Assertions.assertTrue(result.getChanged().contains("changed2"));
    Assertions.assertTrue(result.getChanged().contains("changed3"));

    Assertions.assertEquals(4, result.getDeleted().size());
    Assertions.assertTrue(result.getDeleted().contains("deleted"));
    Assertions.assertTrue(result.getDeleted().contains("deleted2"));
    Assertions.assertTrue(result.getDeleted().contains("obsolete"));
    Assertions.assertTrue(result.getDeleted().contains("obsolete2"));
  }
}
