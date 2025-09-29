package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import de.bund.digitalservice.ris.search.exception.OpenSearchFetchException;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import de.bund.digitalservice.ris.search.models.errors.CustomErrorResponse;
import de.bund.digitalservice.ris.search.service.exception.XMLElementNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  public static final String CODE_FOR_403 = "forbidden";
  public static final String CODE_FOR_404 = "not_found";

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public final ResponseEntity<CustomErrorResponse> handleException(
      MethodArgumentNotValidException ex) {
    List<CustomError> errors =
        ex.getBindingResult().getAllErrors().stream().map(this::sanitizeError).toList();
    CustomErrorResponse errorResponse = CustomErrorResponse.builder().errors(errors).build();
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public final ResponseEntity<CustomErrorResponse> handleException(
      ConstraintViolationException ex) {

    List<CustomError> violations =
        ex.getConstraintViolations().stream()
            .map(
                violation -> {
                  var iterator = violation.getPropertyPath().iterator();
                  Path.Node lastNode = null;
                  while (iterator.hasNext()) {
                    lastNode = iterator.next();
                  }
                  String propertyName = "";
                  if (!Objects.isNull(lastNode)) {
                    propertyName = lastNode.getName();
                  }
                  return new CustomError("invalid parameter", violation.getMessage(), propertyName);
                })
            .toList();

    CustomErrorResponse errorResponse = CustomErrorResponse.builder().errors(violations).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  private CustomError sanitizeError(ObjectError error) {
    if (error instanceof FieldError fieldError) {
      return new CustomError(
          "invalid_parameter_value", fieldError.getDefaultMessage(), fieldError.getField());
    } else {
      return new CustomError("unknown", "Unknown error", "");
    }
  }

  @ExceptionHandler(CustomValidationException.class)
  public ResponseEntity<CustomErrorResponse> handleCustomValidationException(
      CustomValidationException exception) {
    var errorResponse = CustomErrorResponse.builder().errors(exception.getErrors()).build();
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }

  /**
   * This method is used to handle a {@link
   * org.springframework.web.bind.MissingServletRequestParameterException} in a way that it also
   * return a {@link CustomErrorResponse} .
   *
   * @param ex the {@link org.springframework.web.bind.MissingServletRequestParameterException} that
   *     is thrown during the validation of a request
   * @return the response entity with the error response
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<CustomErrorResponse> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex) {
    var errorDetail =
        CustomError.builder()
            .code("information_missing")
            .parameter(ex.getParameterName())
            .message(ex.getMessage())
            .build();
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(errorDetail)).build();
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    FileTransformationException.class,
    Exception.class
  })
  public ResponseEntity<CustomErrorResponse> handleMissingServletRequestParameter(Exception ex) {
    logger.error(ex.getMessage(), ex);
    return return500();
  }

  /**
   * log error when requested xml elements are not found
   *
   * @param ex {@link XMLElementNotFoundException}
   * @return {@link org.springframework.http.ResponseEntity} with empty body
   */
  @ExceptionHandler(XMLElementNotFoundException.class)
  public ResponseEntity<CustomErrorResponse> handleMissingServletRequestParameter(
      XMLElementNotFoundException ex) {

    logger.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    logger.warn(ex.getMessage());
    return return403();
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<CustomErrorResponse> handleRouteNotFoundException(
      NoResourceFoundException ex) {
    logger.warn(ex.getMessage());
    CustomError error = new CustomError(CODE_FOR_404, "The requested data could not be found", "");
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(error)).build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(OpenSearchFetchException.class)
  public ResponseEntity<CustomErrorResponse> handleElasticsearchException(
      OpenSearchFetchException ex) {
    logger.error("Opensearch fetch error", ex);
    CustomError error =
        new CustomError(
            HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            "The requested data could not be fetched.",
            "");
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(error)).build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<CustomErrorResponse> handleUnsupportedOperationException(
      UnsupportedOperationException ex) {
    logger.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  public static ResponseEntity<CustomErrorResponse> return403() {
    CustomError error =
        CustomError.builder()
            .code(CODE_FOR_403)
            .message(
                "Access is not allowed. This includes some cases of improper access such as wrong method.")
            .parameter("")
            .build();
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(error)).build();
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  public static ResponseEntity<CustomErrorResponse> return429() {
    CustomError error =
        CustomError.builder()
            .code("too_many_requests")
            .message("Too many requests. Please try again later")
            .parameter("")
            .build();
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(error)).build();
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
  }

  public static ResponseEntity<CustomErrorResponse> return500() {
    CustomError error =
        new CustomError(
            "internal_error", "An unexpected error occurred. Please try again later.", "");
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(error)).build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
