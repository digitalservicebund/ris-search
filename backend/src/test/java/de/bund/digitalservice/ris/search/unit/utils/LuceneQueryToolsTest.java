package de.bund.digitalservice.ris.search.unit.utils;

import static de.bund.digitalservice.ris.search.utils.LuceneQueryTools.checkForInvalidQuery;
import static de.bund.digitalservice.ris.search.utils.LuceneQueryTools.isValidLuceneQuery;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import java.io.IOException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Response;
import org.opensearch.client.ResponseException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

class LuceneQueryToolsTest {

  @Test
  void testIsValidLuceneQuery() {
    Assertions.assertTrue(isValidLuceneQuery("test"));
    Assertions.assertTrue(isValidLuceneQuery("decision_date:2024-02-01"));
    Assertions.assertFalse(isValidLuceneQuery("(test"));
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

    CustomValidationException thrownException =
        Assertions.assertThrowsExactly(
            CustomValidationException.class, () -> checkForInvalidQuery(outerException));
    Assertions.assertEquals("sort", thrownException.getErrors().get(0).parameter());
    Assertions.assertEquals(
        "Sorting is not supported for param_field", thrownException.getErrors().get(0).message());
  }
}
