package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import java.util.AbstractMap;
import java.util.Map;

public abstract class Courts {

  private Courts() {}

  public static final Map<String, String> supportedCourtNames =
      Map.ofEntries(
          new AbstractMap.SimpleEntry<>("BGH", "Bundesgerichtshof"),
          new AbstractMap.SimpleEntry<>("BVerwG", "Bundesverwaltungsgericht"),
          new AbstractMap.SimpleEntry<>("BVerfG", "Bundesverfassungsgericht"),
          new AbstractMap.SimpleEntry<>("BFH", "Bundesfinanzhof"),
          new AbstractMap.SimpleEntry<>("BAG", "Bundesarbeitsgericht"),
          new AbstractMap.SimpleEntry<>("BSG", "Bundessozialgericht"),
          new AbstractMap.SimpleEntry<>("BPatG", "Bundespatentgericht"));
}
