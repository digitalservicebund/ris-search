package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Utility class for parsing and merging multiple changelogs. */
public class BulkChangelogParser {

  private BulkChangelogParser() {}

  public static boolean containsChangeAll(List<Changelog> logs) {
    return !logs.stream().filter(Changelog::isChangeAll).toList().isEmpty();
  }

  /**
   * Merges multiple changelogs into a single changelog. If a file is marked as changed in one
   * changelog and deleted in another, it will be marked as changed in the merged changelog.
   *
   * @param changelogs the list of changelogs to merge
   * @return the merged changelog
   */
  public static Changelog mergeChangelogs(List<Changelog> changelogs) {
    enum Action {
      CHANGED,
      DELETED
    }
    Map<String, Action> mergedChanges = new HashMap<>();
    for (Changelog log : changelogs) {
      log.getChanged().forEach(filename -> mergedChanges.put(filename, Action.CHANGED));
      log.getDeleted().forEach(filename -> mergedChanges.put(filename, Action.DELETED));
    }
    Changelog mergedChangelog = new Changelog();
    mergedChanges.forEach(
        (id, type) -> {
          if (Action.CHANGED.equals(type)) {
            mergedChangelog.getChanged().add(id);
          } else {
            mergedChangelog.getDeleted().add(id);
          }
        });

    return mergedChangelog;
  }
}
