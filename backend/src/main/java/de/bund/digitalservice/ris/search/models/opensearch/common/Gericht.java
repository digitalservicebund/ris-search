package de.bund.digitalservice.ris.search.models.opensearch.common;

import de.bund.digitalservice.ris.search.utils.MappingUtils;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** The court of a referenced decision (ris:gericht). */
@Builder
public record Gericht(
    @Field(name = "gerichtstyp") String gerichtstyp,
    @Field(name = "gerichtsort") String gerichtsort,
    @Field(name = "gerichtsbarkeit") String gerichtsbarkeit,
    @Field(name = "spruchkoerper") String spruchkoerper,
    @Field(name = "sitz_der_aussenstelle") String sitzDerAussenstelle) {

  /**
   * Constructs a string from all relevant properties that can be used for indexing.
   *
   * @return a string representing the Gericht for searching
   */
  public String searchText() {
    return MappingUtils.joinNonBlank(
        gerichtstyp, gerichtsort, gerichtsbarkeit, spruchkoerper, sitzDerAussenstelle);
  }
}
