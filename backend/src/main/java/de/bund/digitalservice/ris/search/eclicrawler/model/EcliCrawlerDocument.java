package de.bund.digitalservice.ris.search.eclicrawler.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/** Model class representing the state of a case law to be crawled or removed by the ecli crawler */
@Builder
@Document(indexName = "ecli_crawler_document")
public record EcliCrawlerDocument(
    @Id @Field(name = "documentNumber") String documentNumber,
    @Field(name = "filename") String filename,
    @Field(name = "ecli") String ecli,
    @Field(name = "court_type") String courtType,
    @Field(name = "decision_date") String decisionDate,
    @Field(name = "document_type") String documentType,
    @Field(name = "url") String url,
    @Field(name = "is_publised") boolean isPublished) {}
