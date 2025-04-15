package de.bund.digitalservice.ris.search.legacyportal.dto.importer;

import java.util.List;
import lombok.Builder;

@Builder
public record LegislationImporterDTO(
    String identifier,
    String name,
    String documentUri,
    String alternateName,
    String docTitle,
    String text,
    String xmlFilePath,
    String printAnnouncementGazette,
    int printAnnouncementYear,
    String printAnnouncementPage,
    List<ContentItemImporterDTO> contentItems,
    String version,
    String globalUID) {}
