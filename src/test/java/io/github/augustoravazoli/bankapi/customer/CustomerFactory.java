package io.github.augustoravazoli.bankapi.customer;

import java.time.LocalDate;
import net.datafaker.Faker;

final class CustomerFactory {

  private static final long ID = 1;
  private static final String NAME = "customer";
  private static final String EMAIL = "customer@example.com";
  private static final String CPF = new Faker().cpf().valid();
  private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 9, 9);

  private CustomerFactory() {}

  public static CustomerRequest createRequest() {
    return new CustomerRequest(NAME, EMAIL, CPF, BIRTH_DATE);
  }

  public static CustomerRequest createRequestMissingName() {
    return new CustomerRequest("", EMAIL, CPF, BIRTH_DATE);
  }

  public static Customer createEntity() {
    return new Customer(ID, NAME, EMAIL, CPF, BIRTH_DATE);
  }

  public static CustomerResponse createResponse() {
    return new CustomerResponse(ID, NAME, EMAIL, CPF, BIRTH_DATE);
  }

}
