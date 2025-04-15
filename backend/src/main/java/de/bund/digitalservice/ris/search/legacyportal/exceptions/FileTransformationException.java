package de.bund.digitalservice.ris.search.legacyportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileTransformationException extends RuntimeException {

  public FileTransformationException(String message) {
    super(message);
  }
}
