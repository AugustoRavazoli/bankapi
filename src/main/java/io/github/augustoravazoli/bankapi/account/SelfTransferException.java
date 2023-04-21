package io.github.augustoravazoli.bankapi.account;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import org.springframework.web.server.ResponseStatusException;

class SelfTransferException extends ResponseStatusException {

  public SelfTransferException() {
    super(UNPROCESSABLE_ENTITY, "can't transfer to the same account");
  }

}
