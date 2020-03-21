package uk.gov.nhs.sts.controller.advice;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice(annotations = {RestController.class})
@Slf4j
public class ExceptionHandlingControllerAdvice {

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,
      reason = "Error while processing the request")
  public ResponseEntity<Void> handleControllerException(final Throwable ex) {
    log.error("Error while processing the request", ex);
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad request")
  public static ResponseEntity<Void> handleJsonPropertyException(
      final HttpMessageNotReadableException ex) {
    log.error("Bad request", ex);
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(code = HttpStatus.CONFLICT, reason = "Resource already exists")
  public static ResponseEntity<Void> handleDataIntegrityViolationException(
      final HttpMessageNotReadableException ex) {
    log.error("Resource already existst", ex);
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

}
