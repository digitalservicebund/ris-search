package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Literature;

/**
 * Repository for Literature documents stored in OpenSearch.
 *
 * <p>Extends the generic DocumentRepository to provide CRUD and query operations for Literature
 * entities.
 */
public interface LiteratureRepository extends DocumentRepository<Literature> {}
