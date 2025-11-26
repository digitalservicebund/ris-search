package de.bund.digitalservice.ris.search.importer.changelog;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a changelog payload used during imports.
 *
 * <p>Contains sets of changed and deleted identifiers and a flag indicating whether all items
 * changed.
 */
@Getter
@Setter
public class Changelog {

  @JsonProperty("changed")
  HashSet<String> changed = new HashSet<>();

  @JsonProperty("deleted")
  HashSet<String> deleted = new HashSet<>();

  @JsonProperty("change_all")
  boolean changeAll;

  /**
   * Create an empty Changelog.
   *
   * <p>Initializes internal collections to empty sets.
   */
  public Changelog() {
    // default constructor initializes fields via their declarations
  }
}
