package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangelogParser {
  public static Changelog mergeChangelogs(List<Changelog> changelogs)
      throws ObjectStoreServiceException {

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
          switch (type) {
            case CHANGED -> mergedChangelog.getChanged().add(id);
            case DELETED -> mergedChangelog.getDeleted().add(id);
          }
        });

    return mergedChangelog;
  }
}
