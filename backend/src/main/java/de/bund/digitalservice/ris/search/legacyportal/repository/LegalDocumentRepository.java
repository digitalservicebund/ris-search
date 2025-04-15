package de.bund.digitalservice.ris.search.legacyportal.repository;

import de.bund.digitalservice.ris.search.legacyportal.model.LegalDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LegalDocumentRepository extends ElasticsearchRepository<LegalDocument, String> {

  LegalDocument getByDocumentUri(String documentUri);

  @Query(
      """
                 {
                    "bool": {
                      "must": {
                        "multi_match": {
                          "query": "?0",
                          "fields": [
                            "title",
                            "content"
                          ],
                          "operator": "and"
                        }
                      },
                      "filter": {
                        "term": {
                          "documenttype.keyword": "?1"
                        }
                      }
                    }
                  }

                """)
  Page<LegalDocument> findByTitleAndContentCustomQuery(
      String search, String documenttype, Pageable pageable);

  static final String CONDITION_TITLE_MATCH =
      """
    "must": [
      { "match": { "title": "?1" }}
    ],
  """;

  static final String CONDITION_FILTER_TERMS =
      """
    "filter": [
      {"term": {
        "documenttype.keyword": "LEGISLATION"
      }},
      {"term": {
        "version": "?0"
      }}
    ]
      """;

  @Query(
      """
      {
        "bool": {
          """
          + CONDITION_FILTER_TERMS
          + """
            , "must": [
              { "match": { "title": "?1" }}
            ]
          }
      }
        """)
  Optional<List<LegalDocument>> findAllLegislationByTitleAndVersionCustomQuery(
      String version, String searchTerm);

  @Query(
      """
    {
      "bool": {
        """ + CONDITION_FILTER_TERMS + """
      }
    }
      """)
  Optional<List<LegalDocument>> findAllLegislationByVersionCustomQuery(String version);
}
