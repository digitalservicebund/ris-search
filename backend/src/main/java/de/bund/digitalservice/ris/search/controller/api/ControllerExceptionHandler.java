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

/**
 * Global exception handler for REST controllers. This class uses {@link ControllerAdvice} to
 * intercept exceptions thrown by controller methods and provide standardized error responses.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  public static final String CODE_FOR_403 = "forbidden";
  public static final String CODE_FOR_404 = "not_found";

  /**
   * Handles exceptions of type {@link MethodArgumentNotValidException} and returns a standardized
   * error response with HTTP status 422 (Unprocessable Entity). This method extracts validation
   * errors from the exception and maps them to a list of custom error objects, which are included
   * in the error response.
   *
   * @param ex the {@link MethodArgumentNotValidException} instance containing validation errors
   *     that need to be processed and returned in the response
   * @return a {@link ResponseEntity} containing a {@link CustomErrorResponse} object with the list
   *     of validation errors and an HTTP status of 422 (Unprocessable Entity)
   */
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public final ResponseEntity<CustomErrorResponse> handleException(
      MethodArgumentNotValidException ex) {
    List<CustomError> errors =
        ex.getBindingResult().getAllErrors().stream().map(this::sanitizeError).toList();
    CustomErrorResponse errorResponse = CustomErrorResponse.builder().errors(errors).build();
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }

  /**
   * Handles exceptions of type {@link ConstraintViolationException} and returns a standardized
   * error response with HTTP status 400 (Bad Request). The method extracts constraint violation
   * details and maps them to a list of custom error objects included in the response.
   *
   * @param ex the {@link ConstraintViolationException} containing the validation errors
   * @return a {@link ResponseEntity} containing a {@link CustomErrorResponse} object with the list
   *     of validation errors and an HTTP status of 400 (Bad Request)
   */
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

  /**
   * Handles exceptions of type {@link CustomValidationException} and returns a standardized error
   * response with HTTP status 422 (Unprocessable Entity).
   *
   * @param exception the {@link CustomValidationException} instance containing validation errors
   *     that need to be processed and returned in the response
   * @return a {@link ResponseEntity} containing a {@link CustomErrorResponse} object with the list
   *     of validation errors and an HTTP status of 422 (Unprocessable Entity)
   */
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

  /**
   * Handles exceptions of types {@link IllegalArgumentException}, {@link
   * FileTransformationException}, and {@link Exception}, logs the error, and returns a standardized
   * error response with HTTP status 500 (Internal Server Error).
   *
   * @param ex the exception instance being handled, which can be an {@link
   *     IllegalArgumentException}, {@link FileTransformationException}, or a generic {@link
   *     Exception}
   * @return a {@link ResponseEntity} containing a {@link CustomErrorResponse} object with error
   *     details and an HTTP status of 500 (Internal Server Error)
   */
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

  /**
   * Handles exceptions of type {@link AccessDeniedException} and returns a standardized error
   * response with HTTP status 403 (Forbidden).
   *
   * @param ex the {@link AccessDeniedException} instance representing an attempt to access a
   *     resource without proper authorization
   * @return a {@link ResponseEntity} containing a {@link CustomErrorResponse} object with error
   *     details and an HTTP status of 403 (Forbidden)
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    logger.warn(ex.getMessage());
    return return403();
  }

  /**
   * Handles exceptions of type {@link NoResourceFoundException} and returns a standardized error
   * response with HTTP status 404 (Not Found).
   *
   * @param ex the {@link NoResourceFoundException} instance representing the error condition when
   *     the requested resource is not found
   * @return a {@link ResponseEntity} containing a {@link CustomErrorResponse} object with error
   *     details and an HTTP status of 404 (Not Found)
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<CustomErrorResponse> handleRouteNotFoundException(
      NoResourceFoundException ex) {
    logger.warn(ex.getMessage());
    CustomError error = new CustomError(CODE_FOR_404, "The requested data could not be found", "");
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(error)).build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * Handles exceptions of type {@link OpenSearchFetchException} and returns a standardized error
   * response with HTTP status 500 (Internal Server Error).
   *
   * @param ex the {@link OpenSearchFetchException} instance thrown during OpenSearch data retrieval
   *     failure
   * @return a {@link ResponseEntity} containing a {@link CustomErrorResponse} object with error
   *     details and an HTTP status of 500
   */
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

  /**
   * Creates and returns a ResponseEntity containing a standardized error response with HTTP status
   * 403. The error response encapsulates an error message indicating that access is not allowed,
   * including cases of improper access such as using the wrong method.
   *
   * @return ResponseEntity containing a CustomErrorResponse object with error details and an HTTP
   *     status of 403 (Forbidden).
   */
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

  /**
   * Creates and returns a ResponseEntity containing a standardized error response with HTTP status
   * 429. The error response encapsulates an error message indicating "Too Many Requests".
   *
   * @return ResponseEntity containing a CustomErrorResponse object with error details and an HTTP
   *     status of 429 (Too Many Requests).
   */
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

  /**
   * Creates and returns a ResponseEntity containing a standardized error response with HTTP status
   * 500. The error response encapsulates an internal server error message.
   *
   * @return ResponseEntity containing a CustomErrorResponse object with error details and an HTTP
   *     status of 500 (Internal Server Error).
   */
  public static ResponseEntity<CustomErrorResponse> return500() {
    CustomError error =
        new CustomError(
            "internal_error", "An unexpected error occurred. Please try again later.", "");
    CustomErrorResponse errorResponse =
        CustomErrorResponse.builder().errors(List.of(error)).build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
