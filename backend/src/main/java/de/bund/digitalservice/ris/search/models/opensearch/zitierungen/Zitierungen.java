package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import java.util.List;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Object to group all citations. */
@Builder
public record Zitierungen(
    @Field(name = "zitierungen_literatur_selbstaendig")
        List<ReferenzSelbststaendigeLiteratur> zitierungenLiteraturSelbstaendig,
    @Field(name = "zitierungen_literatur_unselbstaendig")
        List<ReferenzUnselbststaendigeLiteratur> zitierungenLiteraturUnselbstaendig,
    @Field(name = "zitierungen_rechtsprechung")
        List<ReferenzRechtsprechung> zitierungenRechtsprechung,
    @Field(name = "zitierungen_verwaltungsvorschriften")
        List<ReferenzVerwaltungsvorschrift> zitierungenVerwaltungsvorschriften) {}
