package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import java.util.List;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Object to group all passive/active citations. */
@Builder
public record Zitierungen(
    @Field(name = "aktivzitierung_literatur_selbstaendig")
        List<ReferenzSelbststaendigeLiteratur> aktivzitierungLiteraturSelbstaendig,
    @Field(name = "aktivzitierung_literatur_unselbstaendig")
        List<ReferenzUnselbststaendigeLiteratur> aktivzitierungLiteraturUnselbstaendig,
    @Field(name = "aktivzitierung_rechtsprechung")
        List<ReferenzRechtsprechung> aktivzitierungRechtsprechung,
    @Field(name = "aktivzitierung_verwaltungsvorschriften")
        List<ReferenzVerwaltungsvorschrift> aktivzitierungVerwaltungsvorschriften,
    @Field(name = "passivzitierung_literatur_selbstaendig")
        List<ReferenzSelbststaendigeLiteratur> passivzitierungLiteraturSelbstaendig,
    @Field(name = "passivzitierung_literatur_unselbstaendig")
        List<ReferenzUnselbststaendigeLiteratur> passivzitierungLiteraturUnselbstaendig,
    @Field(name = "passivzitierung_rechtsprechung")
        List<ReferenzRechtsprechung> passivzitierungRechtsprechung,
    @Field(name = "passivzitierung_verwaltungsvorschriften")
        List<ReferenzVerwaltungsvorschrift> passivzitierungVerwaltungsvorschriften) {}
