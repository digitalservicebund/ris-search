package de.bund.digitalservice.ris.search.api.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;

/**
 * A record representing a minimal <a href="https://schema.org/PublicationIssue">schema.org
 * PublicationIssue</a> type
 */
@JsonldType("PublicationIssue")
public record PublicationIssueSchema(@Schema(example = "BGBL I 2003, 1760") String name) {}
