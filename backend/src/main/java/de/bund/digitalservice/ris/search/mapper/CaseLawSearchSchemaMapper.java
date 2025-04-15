package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import java.util.List;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

public class CaseLawSearchSchemaMapper {
  private CaseLawSearchSchemaMapper() {}

  public static <T> SearchMemberSchema<CaseLawSearchSchema> fromSearchHit(SearchHit<T> searchHit) {
    CaseLawDocumentationUnit document = (CaseLawDocumentationUnit) searchHit.getContent();
    List<TextMatchSchema> textMatches =
        searchHit.getHighlightFields().entrySet().stream()
            .map(
                textMatch ->
                    TextMatchSchema.builder()
                        .name(textMatch.getKey())
                        .text(textMatch.getValue().getFirst())
                        .build())
            .toList();

    return SearchMemberSchema.<CaseLawSearchSchema>builder()
        .item(fromDomain(document))
        .textMatches(textMatches)
        .build();
  }

  public static CaseLawSearchSchema fromDomain(CaseLawDocumentationUnit doc) {
    String entityURI = ApiConfig.Paths.CASELAW + "/" + doc.documentNumber();
    return CaseLawSearchSchema.builder()
        // JSON-LD-specific fields
        .id(entityURI)
        .inLanguage("de")
        // links to other resource representations
        // equivalent fields
        .documentNumber(doc.documentNumber())
        .ecli(doc.ecli())
        .headline(doc.headline())
        .otherLongText(doc.otherLongText())
        .decisionDate(doc.decisionDate())
        .fileNumbers(doc.fileNumbers())
        .courtType(doc.courtType())
        .location(doc.location())
        .documentType(doc.documentType())
        .outline(doc.outline())
        .judicialBody(doc.judicialBody())
        .decisionName(doc.decisionName())
        .deviatingDocumentNumber(doc.deviatingDocumentNumber())
        // .publicationStatus(doc.publicationStatus())
        // fields with different name
        .courtName(doc.courtKeyword())
        // end
        .build();
  }

  public static <T> CollectionSchema<SearchMemberSchema<CaseLawSearchSchema>> fromSearchPage(
      final SearchPage<T> page) {
    String collectionBasePath = ApiConfig.Paths.CASELAW;
    PartialCollectionViewSchema view =
        PartialCollectionViewMapper.fromPage(collectionBasePath, page);

    String id =
        String.format("%s?page=%d&size=%d", collectionBasePath, page.getNumber(), page.getSize());

    return CollectionSchema.<SearchMemberSchema<CaseLawSearchSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(CaseLawSearchSchemaMapper::fromSearchHit).toList())
        .view(view)
        .build();
  }
}
