package de.bund.digitalservice.ris.search.config.httplogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Representation of a http requestLog LogMessage
 *
 * @param path path of an incoming request
 * @param queryParams query paramters of an incoming request
 * @param responseStatusCode resulting http code of the request
 */
@JsonSerialize
public record RequestLog(
    @JsonProperty("path") String path,
    @JsonProperty("queryParams") String queryParams,
    @JsonProperty("status") int responseStatusCode) {}
