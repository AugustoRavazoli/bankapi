package io.github.augustoravazoli.bankapi;

import java.util.List;
import static java.util.stream.Collectors.toList;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

@ControllerAdvice
class GlobalExceptionHandler {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private static record ErrorResponse(String message, List<ErrorDetails> errors) {

    public ErrorResponse(String message) {
      this(message, null);
    }

  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private static record ErrorDetails(String field, String message) {}

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException ex) {
    var errorInfo = new ErrorResponse("validation errors on your request body", ex
      .getBindingResult()
      .getFieldErrors()
      .stream()
      .map(fieldError -> new ErrorDetails(
        fieldError.getField(),
        fieldError.getDefaultMessage()
      ))
      .collect(toList())
    );
    return ResponseEntity
      .unprocessableEntity()
      .body(errorInfo);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handle(ConstraintViolationException ex) {
    var errorInfo = new ErrorResponse("validation errors on your request query parameters", ex
      .getConstraintViolations()
      .stream()
      .map(violation -> new ErrorDetails(
        getFieldFromPath(violation.getPropertyPath()),
        violation.getMessage()
      ))
      .collect(toList())
    );
    return ResponseEntity
      .unprocessableEntity()
      .body(errorInfo);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handle(ResponseStatusException ex) {
    return ResponseEntity
      .status(ex.getStatusCode())
      .body(new ErrorResponse(ex.getReason()));
  }

  private String getFieldFromPath(Path path) {
    return ((PathImpl) path).getLeafNode().toString();
  }

}
