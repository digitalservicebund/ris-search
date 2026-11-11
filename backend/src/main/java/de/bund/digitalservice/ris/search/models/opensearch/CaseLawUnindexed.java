package de.bund.digitalservice.ris.search.models.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturSelbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungVerwaltungsvorschrift;
import java.util.List;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * Container for case law data that is stored with the document but not indexed in OpenSearch
 * (mapped with {@code enabled: false}). Its fields can be retrieved, but not searched, filtered,
 * sorted or aggregated on.
 */
@Builder
public record CaseLawUnindexed(
    @Field(name = "zitierungen_literatur_selbststaendig")
        List<ZitierungLiteraturSelbststaendig> zitierungenLiteraturSelbststaendig,
    @Field(name = "zitierungen_literatur_unselbststaendig")
        List<ZitierungLiteraturUnselbststaendig> zitierungenLiteraturUnselbststaendig,
    @Field(name = "zitierungen_rechtsprechung")
        List<ZitierungRechtsprechung> zitierungenRechtsprechung,
    @Field(name = "zitierungen_verwaltungsvorschriften")
        List<ZitierungVerwaltungsvorschrift> zitierungenVerwaltungsvorschriften,
    @Field(name = "vorgehende_entscheidungen")
        List<VorgehendeEntscheidung> vorgehendeEntscheidungen,
    @Field(name = "nachgehende_entscheidungen")
        List<NachgehendeEntscheidung> nachgehendeEntscheidungen) {}
