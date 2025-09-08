package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangelogParser {

  private ChangelogParser() {}

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
