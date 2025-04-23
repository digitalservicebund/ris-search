package de.bund.digitalservice.ris.search.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.bund.digitalservice.ris.search.api.schema.TextMatchSchema;
import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.elasticsearch.core.SearchHit;

class NormSearchResponseMapperTest {
  private static Article createArticle(String name, String text) {
    return Article.builder().eId("eid1").name(name).text(text).build();
  }

  private TextMatchSchema invokeConvertArticleHitToTextMatchSchema(SearchHit<?> articleHit)
      throws Exception {
    Method method =
        NormSearchResponseMapper.class.getDeclaredMethod(
            "convertArticleHitToTextMatchSchema", SearchHit.class);
    method.setAccessible(true);
    return (TextMatchSchema) method.invoke(null, articleHit);
  }

  private <T> SearchHit<T> createSearchHit(T content, Map<String, List<String>> highlightFields) {
    return new SearchHit<>(
        "1", "1", "routing", 1, null, highlightFields, null, null, null, null, content);
  }

  @ParameterizedTest
  @MethodSource("provideConvertArticleHitToTextMatchSchemaTestCases")
  void testConvertArticleHitToTextMatchSchema(
      Object content,
      Map<String, List<String>> highlightFields,
      String expectedName,
      String expectedText,
      boolean expectNull)
      throws Exception {

    TextMatchSchema result =
        invokeConvertArticleHitToTextMatchSchema(createSearchHit(content, highlightFields));

    if (expectNull) {
      assertNull(result);
    } else {
      assertNotNull(result);
      assertEquals("eid1", result.location());
      assertEquals(expectedName, result.name());
      assertEquals(expectedText, result.text());
    }
  }

  /** Provides test cases for the parameterized test method. */
  static Stream<Arguments> provideConvertArticleHitToTextMatchSchemaTestCases() {
    return Stream.of(
        // Test Case 1: Article content with empty highlights
        Arguments.of(
            createArticle("Article Name", "Article Text"),
            Map.of(),
            "Article Name",
            "Article Text",
            false),
        // Test Case 2: Article content with name highlight
        Arguments.of(
            createArticle("Original Article Name", "Article Text"),
            Map.of("articles.name", List.of("Highlighted Article Name")),
            "Highlighted Article Name",
            "Article Text",
            false),
        // Test Case 3: Article content with text highlight
        Arguments.of(
            createArticle("Article Name", "Original Article Text"),
            Map.of("articles.text", List.of("Highlighted Article Text")),
            "Article Name",
            "Highlighted Article Text",
            false),
        // Test Case 4: Article content with both name and text highlights
        Arguments.of(
            createArticle("Original Article Name", "Original Article Text"),
            Map.of(
                "articles.name", List.of("Highlighted Article Name"),
                "articles.text", List.of("Highlighted Article Text")),
            "Highlighted Article Name",
            "Highlighted Article Text",
            false),
        // Test Case 5: Map content with "name" key and no text highlight
        Arguments.of(
            Map.of("name", "Article Name", "eid", "eid1"), Map.of(), "Article Name", "", false),
        // Test Case 6: Map content with "name" and "eid" keys and text highlight
        Arguments.of(
            Map.of("name", "Article Name", "eid", "eid1"),
            Map.of("articles.text", List.of("Highlighted Map Article Text")),
            "Article Name",
            "Highlighted Map Article Text",
            false),
        // Test Case 7: Map content without "name" key
        Arguments.of(
            Map.of("otherKey", "Some Value"),
            Map.of("articles.text", List.of("Highlighted Map Article Text")),
            null,
            null,
            true),
        // Test Case 8: Content that is neither Article nor Map with "name" key
        Arguments.of(
            "Some Content",
            Map.of("articles.text", List.of("Highlighted Text")),
            null,
            null,
            true));
  }
}
