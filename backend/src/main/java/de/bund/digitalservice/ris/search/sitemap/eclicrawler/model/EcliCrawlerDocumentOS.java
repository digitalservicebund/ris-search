package de.bund.digitalservice.ris.search.sitemap.eclicrawler.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Model class representing a case law to opensearch index. This class is annotated with Lombok
 * annotations for generating getters, setters, constructors, and builder methods.
 */
@Builder
@Document(indexName = "ecli_crawler_document")
@Setting(settingPath = "/openSearch/german_analyzer.json")
@Mapping(mappingPath = "/openSearch/caselaw_mappings.json")
public record EcliCrawlerDocumentOS(
    @Id @Field(name = "id") String id,
    @Field(name = "ecli") String ecli,
    @Field(name = "court_type") String courtType,
    @Field(name = "decision_date") String decisionDate,
    @Field(name = "document_type") String documentType,
    @Field(name = "is_publised") boolean isPublished) {

  public boolean metadataEquals(EcliCrawlerDocumentOS other) {
    return other.id().equals(this.id)
        && other.ecli.equals(this.ecli)
        && other.courtType.equals(this.courtType)
        && other.decisionDate.equals(this.decisionDate)
        && other.documentType.equals(this.documentType);
  }
}
