package io.github.augustoravazoli.bankapi.account;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.web.server.ResponseStatusException;

class AccountMismatchException extends ResponseStatusException {

  public AccountMismatchException() {
    super(NOT_FOUND, "account not found");
  }

}
