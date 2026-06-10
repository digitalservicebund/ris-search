package de.bund.digitalservice.ris.search.unit.utils;

import static de.bund.digitalservice.ris.search.utils.LuceneQueryTools.checkForInvalidQuery;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.utils.LuceneQueryTools;
import java.io.IOException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Response;
import org.opensearch.client.ResponseException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

class LuceneQueryToolsTest {

  @Test
  void testIsValidLuceneQuery() {
    assertThatCode(() -> LuceneQueryTools.validateLuceneQuery("test")).doesNotThrowAnyException();
    assertThatCode(() -> LuceneQueryTools.validateLuceneQuery("decision_date:2024-02-01"))
        .doesNotThrowAnyException();
    assertThatExceptionOfType(CustomValidationException.class)
        .isThrownBy(() -> LuceneQueryTools.validateLuceneQuery("(test"));
  }

  @Test
  void testCheckForInvalidQueryException() throws IOException {
    Response mockResponse = mock(Response.class);
    when(mockResponse.getRequestLine())
        .thenReturn(new RequestLine("GET", "uri", new ProtocolVersion("HTTP", 1, 1)));

    when(mockResponse.getStatusLine())
        .thenReturn(
            new StatusLine(
                new ProtocolVersion("HTTP", 1, 1),
                422,
                "No mapping found for [param_field] in order to sort on"));

    var outerException = mock(UncategorizedElasticsearchException.class);
    var innerException = new Exception("message");
    innerException.addSuppressed(new ResponseException(mockResponse));
    when(outerException.getCause()).thenReturn(innerException);

    assertThatExceptionOfType(CustomValidationException.class)
        .isThrownBy(() -> checkForInvalidQuery(outerException))
        .satisfies(
            e -> {
              assertThat(e.getErrors().getFirst().parameter()).isEqualTo("sort");
              assertThat(e.getErrors().getFirst().message())
                  .isEqualTo("Sorting is not supported for param_field");
            });
  }

  @Test
  void returnsEmptyStringForNull() throws CustomValidationException {
    assertThat(LuceneQueryTools.joinAllTermsWithOr(null)).isEmpty();
  }

  @Test
  void returnsEmptyStringForBlankInput() throws CustomValidationException {
    assertThat(LuceneQueryTools.joinAllTermsWithOr("   ")).isEmpty();
  }

  @Test
  void returnsSingleTerm() throws CustomValidationException {
    assertThat(LuceneQueryTools.joinAllTermsWithOr("test")).isEqualTo("test");
  }

  @Test
  void joinsMultipleTermsWithOr() throws CustomValidationException {
    var result = LuceneQueryTools.joinAllTermsWithOr("a:foo b:bar");
    assertThat(result.split(" OR ")).containsExactlyInAnyOrder("a:foo", "b:bar");
  }

  @Test
  void extractsTermsFromAndQuery() throws CustomValidationException {
    var result = LuceneQueryTools.joinAllTermsWithOr("foo AND bar");
    assertThat(result.split(" OR ")).containsExactlyInAnyOrder("foo", "bar");
  }

  @Test
  void preservesFieldPrefixAndEscapesSpecialCharacters() throws CustomValidationException {
    assertThat(LuceneQueryTools.joinAllTermsWithOr("decision_date:2024-02-01"))
        .isEqualTo("decision_date:2024\\-02\\-01");
  }

  @Test
  void throwsForInvalidLuceneSyntax() {
    assertThatExceptionOfType(CustomValidationException.class)
        .isThrownBy(() -> LuceneQueryTools.joinAllTermsWithOr("(test"));
  }
}
