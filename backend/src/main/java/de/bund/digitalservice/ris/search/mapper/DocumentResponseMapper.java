package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.schema.AbstractDocumentSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.PartialCollectionViewSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import java.util.List;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

public class DocumentResponseMapper {
  private DocumentResponseMapper() {}

  public static <T> SearchMemberSchema<AbstractDocumentSchema> convertSingle(
      SearchHit<T> searchHit) {
    List<TextMatchSchema> textMatches;
    AbstractDocumentSchema document;
    if (searchHit.getContent() instanceof CaseLawDocumentationUnit cldu) {
      textMatches = CaseLawSearchSchemaMapper.getTextMatches(searchHit);
      document = CaseLawSearchSchemaMapper.fromDomain(cldu);
    } else if (searchHit.getContent() instanceof Literature literature) {
      textMatches = LiteratureSearchSchemaMapper.getTextMatches(searchHit);
      document = LiteratureSearchSchemaMapper.fromDomain(literature);
    } else if (searchHit.getContent() instanceof Norm norm) {
      textMatches = NormSearchResponseMapper.getTextMatches(searchHit);
      document = NormSearchResponseMapper.fromDomain(norm);
    } else {
      throw new IllegalArgumentException(
          "Unknown entity type: " + searchHit.getContent().getClass().getSimpleName());
    }

    return SearchMemberSchema.builder().item(document).textMatches(textMatches).build();
  }

  public static <T> CollectionSchema<SearchMemberSchema<AbstractDocumentSchema>> fromDomain(
      final SearchPage<T> page, String path) {
    String id = String.format("%s?pageIndex=%d&size=%d", path, page.getNumber(), page.getSize());
    PartialCollectionViewSchema view = PartialCollectionViewMapper.fromPage(path, page);

    return CollectionSchema.<SearchMemberSchema<AbstractDocumentSchema>>builder()
        .id(id)
        .totalItems(page.getTotalElements())
        .member(page.stream().map(DocumentResponseMapper::convertSingle).toList())
        .view(view)
        .build();
  }
}
