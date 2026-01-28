package de.bund.digitalservice.ris.search.config.opensearch;

import java.io.IOException;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OpenSearchRetryStrategy implements HttpRequestRetryStrategy {

  private static final Logger logger = LogManager.getLogger(OpenSearchRetryStrategy.class);

  private static final int MAXIMUM_TRY_COUNT = 3;
  private final TimeValue retryInterval = TimeValue.ofMilliseconds(1000);

  @Override
  public boolean retryRequest(
      HttpRequest request, IOException exception, int execCount, HttpContext context) {
    if (execCount < MAXIMUM_TRY_COUNT) {
      logger.warn(
          "Call {} of {} to Opensearch failed with an exception. Will retry. request : {}. Exception :",
          execCount,
          MAXIMUM_TRY_COUNT,
          request,
          exception);
      return true;
    } else {
      logger.error(
          "All calls to Opensearch failed with an exception. Will not retry. request : {}. Exception :",
          request,
          exception);
      return false;
    }
  }

  @Override
  public boolean retryRequest(HttpResponse response, int execCount, HttpContext context) {
    if (execCount < MAXIMUM_TRY_COUNT) {
      logger.warn(
          "Call {} of {} to Opensearch failed with a response from Opensearch. Will retry. Response : {}.",
          execCount,
          MAXIMUM_TRY_COUNT,
          response);
      return true;
    } else {
      logger.error(
          "All calls to Opensearch failed with a response from Opensearch. Will retry. Response : {}.",
          response);
      return false;
    }
  }

  @Override
  public TimeValue getRetryInterval(HttpResponse response, int execCount, HttpContext context) {
    return retryInterval;
  }
}
