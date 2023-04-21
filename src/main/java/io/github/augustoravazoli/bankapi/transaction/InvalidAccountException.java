package io.github.augustoravazoli.bankapi.transaction;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import org.springframework.web.server.ResponseStatusException;

class InvalidAccountException extends ResponseStatusException {

  public InvalidAccountException(String message) {
    super(UNPROCESSABLE_ENTITY, message);
  }

}
