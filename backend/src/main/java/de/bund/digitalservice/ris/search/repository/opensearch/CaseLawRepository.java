package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link
 * CaseLawDocumentationUnit} entity. This interface extends {@link ElasticsearchRepository} and
 * focuses on operations related to {@link CaseLawDocumentationUnit}.
 */
public interface CaseLawRepository
    extends ElasticsearchRepository<CaseLawDocumentationUnit, String> {
  List<CaseLawDocumentationUnit> findByDocumentNumber(String documentNumber);

  void deleteByIndexedAtBefore(String indexedAt);

  void deleteByIndexedAtIsNull();

  void deleteAllById(Iterable<? extends String> ids);

  @Query(
      """
          {
              "bool":{
                "must":[
                  {"exists" :{"field": "ecli"}},
                  {"exists" :{"field": "document_type"}},
                  {"exists" :{"field": "decision_date"}},
                  {"terms": {"court_type.keyword":["BGH", "BVerwG", "BVerfG", "BFH", "BAG", "BSG", "BPatG"] }},
                  {"terms": {"id": #{#documentNumbers}}}
                ]}}
          """)
  List<CaseLawDocumentationUnit> findAllValidFederalEcliDocumentsIn(List<String> documentNumbers);

  @Query(
      """
          {
              "bool":{
                "must":[
                  {"exists" :{"field": "ecli"}},
                  {"exists" :{"field": "document_type"}},
                  {"exists" :{"field": "decision_date"}},
                  {"terms": {"court_type.keyword":["BGH", "BVerwG", "BVerfG", "BFH", "BAG", "BSG", "BPatG"] }}
                ]}}
          """)
  Stream<CaseLawDocumentationUnit> findAllValidFederalEcliDocuments();
}
