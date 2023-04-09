package io.github.augustoravazoli.bankapi;

import java.util.List;
import static java.util.stream.Collectors.toList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.annotation.JsonInclude;

@ControllerAdvice
public class GlobalExceptionHandler {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static record ErrorResponse(String message, List<ErrorDetails> errors) {

    public ErrorResponse(String message) {
      this(message, null);
    }

  }

  private static record ErrorDetails(String field, String message) {}

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException ex) {
    var errorInfo = new ErrorResponse("validation errors", ex
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

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handle(ResponseStatusException ex) {
    return ResponseEntity
      .status(ex.getStatusCode())
      .body(new ErrorResponse(ex.getReason()));
  }

}
