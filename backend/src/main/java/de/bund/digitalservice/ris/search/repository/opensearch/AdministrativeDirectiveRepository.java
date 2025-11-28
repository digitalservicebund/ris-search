package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;

/**
 * Repository for AdministrativeDirective documents stored in OpenSearch.
 *
 * <p>Provides CRUD and query operations for AdministrativeDirective entities backed by the
 * DocumentRepository abstraction.
 */
public interface AdministrativeDirectiveRepository
    extends DocumentRepository<AdministrativeDirective> {}
