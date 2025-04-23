package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.api.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.api.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.api.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

public class DocumentResponseMapper {
  private DocumentResponseMapper() {}

  public static <T> SearchMemberSchema convertSingle(SearchHit<T> searchHit) {
    if (searchHit.getContent() instanceof CaseLawDocumentationUnit) {
      return CaseLawSearchSchemaMapper.fromSearchHit(searchHit);
    } else if (searchHit.getContent() instanceof Norm) {
      return NormSearchResponseMapper.fromSearchHit(searchHit);
    } else {
      throw new IllegalArgumentException(
          "Unknown entity type: " + searchHit.getContent().getClass().getSimpleName());
    }
  }

  public static <T> CollectionSchema<SearchMemberSchema> fromDomain(
      final SearchPage<T> page, String path) {
    String id = String.format("%s?page=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<SearchMemberSchema>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(DocumentResponseMapper::convertSingle).toList())
        .view(view)
        .build();
  }
}
