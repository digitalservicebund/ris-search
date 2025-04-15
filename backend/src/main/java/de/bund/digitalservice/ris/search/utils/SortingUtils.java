package de.bund.digitalservice.ris.search.utils;

import java.util.Set;

public class SortingUtils {
  private SortingUtils() {}

  private static final String DATE = "DATUM";

  public static final Set<String> documentSortFields =
      Set.of("date", DATE, "courtName", "documentNumber", "legislationIdentifier");
  public static final Set<String> caseLawSortFields =
      Set.of("date", DATE, "courtName", "documentNumber");
  public static final Set<String> normsSortFields = Set.of("date", DATE, "legislationIdentifier");
}
