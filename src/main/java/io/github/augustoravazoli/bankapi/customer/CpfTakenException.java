package io.github.augustoravazoli.bankapi.customer;

import static org.springframework.http.HttpStatus.CONFLICT;
import org.springframework.web.server.ResponseStatusException;

class CpfTakenException extends ResponseStatusException {

  public CpfTakenException() {
    super(CONFLICT, "cpf number already in use");
  }

}
