package io.github.augustoravazoli.bankapi.account;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import org.springframework.web.server.ResponseStatusException;

class InsufficientBalanceException extends ResponseStatusException {

  public InsufficientBalanceException() {
    super(UNPROCESSABLE_ENTITY, "insufficient balance");
  }

}
