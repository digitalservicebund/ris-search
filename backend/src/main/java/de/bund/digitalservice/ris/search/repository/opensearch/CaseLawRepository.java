package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link
 * CaseLawDocumentationUnit} entity. This interface extends {@link ElasticsearchRepository} and
 * focuses on operations related to {@link CaseLawDocumentationUnit}.
 */
public interface CaseLawRepository extends DocumentRepository<CaseLawDocumentationUnit> {}
