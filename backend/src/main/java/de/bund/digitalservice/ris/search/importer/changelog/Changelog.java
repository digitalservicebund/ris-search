package de.bund.digitalservice.ris.search.importer.changelog;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Changelog {

  @JsonProperty("changed")
  HashSet<String> changed = new HashSet<>();

  @JsonProperty("deleted")
  HashSet<String> deleted = new HashSet<>();

  @JsonProperty("change_all")
  boolean changeAll;
}
