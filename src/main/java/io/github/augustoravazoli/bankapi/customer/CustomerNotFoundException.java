package io.github.augustoravazoli.bankapi.customer;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.web.server.ResponseStatusException;

class CustomerNotFoundException extends ResponseStatusException {

  public CustomerNotFoundException() {
    super(NOT_FOUND, "customer with given cpf not found");
  }

}
