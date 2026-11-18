package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import de.bund.digitalservice.ris.search.utils.MappingUtils;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** A reference to an administrative directive (ris:referenzVerwaltungsvorschrift). */
@Builder
public record ZitierungVerwaltungsvorschrift(
    @Field(name = "richtung") Richtung richtung,
    @Field(name = "referenz_uri") String referenzUri,
    @Field(name = "dokumentnummer") String dokumentnummer,
    @Field(name = "art_der_zitierung") String artDerZitierung,
    @Field(name = "juris_abkuerzung") String jurisAbkuerzung,
    @Field(name = "fundstelle") Fundstelle fundstelle)
    implements Zitierung {

  @Override
  public String searchText() {
    return MappingUtils.joinNonBlank(
        dokumentnummer,
        artDerZitierung,
        jurisAbkuerzung,
        MappingUtils.nullSafeGet(fundstelle, Fundstelle::searchText));
  }
}
