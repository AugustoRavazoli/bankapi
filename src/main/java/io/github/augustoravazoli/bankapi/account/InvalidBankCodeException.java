package io.github.augustoravazoli.bankapi.account;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import org.springframework.web.server.ResponseStatusException;

class InvalidBankCodeException extends ResponseStatusException {

  public InvalidBankCodeException() {
    super(UNPROCESSABLE_ENTITY, "invalid bank code");
  }

}
