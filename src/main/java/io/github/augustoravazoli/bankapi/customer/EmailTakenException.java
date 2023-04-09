package io.github.augustoravazoli.bankapi.customer;

import static org.springframework.http.HttpStatus.CONFLICT;
import org.springframework.web.server.ResponseStatusException;

class EmailTakenException extends ResponseStatusException {

  public EmailTakenException() {
    super(CONFLICT, "email address already in use");
  }

}
