package de.bund.digitalservice.ris.search.config.httplogs;

import de.bund.digitalservice.ris.search.utils.HttpLog;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

/** Custom Logbook Sink that combines logging of both HTTP requests and responses. */
@AllArgsConstructor
public class LogRequestAndResponseTogetherSink implements Sink {
  private final HttpLogFormatter formatter;
  private final HttpLogWriter writer;
  private static final String REGULAR_EXPRESSION_ACTUATOR_PATH = "/actuator/(.*)";

  @Override
  public boolean isActive() {
    return writer.isActive();
  }

  /**
   * Writes log entries for HTTP requests.
   *
   * @param precorrelation the precorrelation identifier
   * @param request the HTTP request
   * @throws IOException if an I/O error occurs
   */
  @SuppressWarnings("null")
  @Override
  public void write(final Precorrelation precorrelation, final HttpRequest request)
      throws IOException {
    /* Overwrite method implemention */
  }

  /**
   * Writes log entries for both HTTP requests and responses.
   *
   * @param correlation the correlation identifier
   * @param request the HTTP request
   * @param response the HTTP response
   * @throws IOException if an I/O error occurs
   */
  @SuppressWarnings("null")
  @Override
  public void write(
      final Correlation correlation, final HttpRequest request, final HttpResponse response)
      throws IOException {
    String requestMessage = formatter.format(correlation, request);
    var reqJson = new JSONObject(requestMessage);
    reqJson = HttpLog.sanitizeLogJson(reqJson);
    reqJson.put("queryParams", HttpLog.getQueryParamsAsMap(request));

    // do not log actuator path
    if (shouldSkipLogging(reqJson.optString("path"))) {
      return;
    }

    String responseMessage = formatter.format(correlation, response);
    var respJson = new JSONObject(responseMessage);
    respJson = HttpLog.sanitizeLogJson(respJson);

    var combinedReqResp = new JSONObject();
    combinedReqResp.put("requestMessage", reqJson);
    combinedReqResp.put("responseMessage", respJson);
    writer.write(correlation, combinedReqResp.toString());
  }

  private boolean shouldSkipLogging(String path) {
    return path.matches(REGULAR_EXPRESSION_ACTUATOR_PATH);
  }
}
