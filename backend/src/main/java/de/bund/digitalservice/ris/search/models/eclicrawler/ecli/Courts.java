package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import java.util.AbstractMap;
import java.util.Map;

/**
 * The Courts class provides a centralized collection of mappings between court abbreviations and
 * their corresponding full German names. This class serves as a utility for managing and retrieving
 * supported court names.
 *
 * <p>Features: - The class is abstract and cannot be instantiated. - Contains a static final map
 * `supportedCourtNames` that holds court abbreviation-to-name mappings.
 *
 * <p>Key-value mappings in `supportedCourtNames` include: - "BGH": Bundesgerichtshof - "BVerwG":
 * Bundesverwaltungsgericht - "BVerfG": Bundesverfassungsgericht - "BFH": Bundesfinanzhof - "BAG":
 * Bundesarbeitsgericht - "BSG": Bundessozialgericht - "BPatG": Bundespatentgericht
 *
 * <p>This utility is designed for static access to facilitate consistent usage of court names
 * within applications and avoid redundancy.
 */
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
