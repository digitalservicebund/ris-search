package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import java.util.List;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Object to group all citations. */
@Builder
public record Zitierungen(
    @Field(name = "zitierungen_literatur_selbststaendig")
        List<ZitierungLiteraturSelbststaendig> zitierungenLiteraturSelbststaendig,
    @Field(name = "zitierungen_literatur_unselbststaendig")
        List<ZitierungLiteraturUnselbststaendig> zitierungenLiteraturUnselbststaendig,
    @Field(name = "zitierungen_rechtsprechung")
        List<ZitierungRechtsprechung> zitierungenRechtsprechung,
    @Field(name = "zitierungen_verwaltungsvorschriften")
        List<ZitierungVerwaltungsvorschrift> zitierungenVerwaltungsvorschriften) {}
