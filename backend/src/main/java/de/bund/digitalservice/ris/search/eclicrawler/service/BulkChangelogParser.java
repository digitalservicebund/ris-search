package de.bund.digitalservice.ris.search.eclicrawler.service;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkChangelogParser {

  private BulkChangelogParser() {}

  public static List<Changelog> getChangelogsFromLastChangeAll(List<Changelog> changelogs) {
    List<Changelog> relevantChangelogs = new ArrayList<>();
    changelogs.forEach(
        changelog -> {
          if (changelog.isChangeAll()) {
            relevantChangelogs.clear();
          } else {
            relevantChangelogs.add(changelog);
          }
        });
    return relevantChangelogs;
  }

  public static boolean containsChangeAll(List<Changelog> logs) {
    return !logs.stream().filter(Changelog::isChangeAll).toList().isEmpty();
  }

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
