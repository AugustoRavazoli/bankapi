package io.github.augustoravazoli.bankapi.account;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
class BankClient {

  private static record BankResponse(String fullName) {}

  private final WebClient client;
  private final BankClientProperties properties;

  @Autowired
  public BankClient(WebClient client, BankClientProperties properties) {
    this.client = client;
    this.properties = properties;
  }

  public String findBankNameByCode(int code) {
    return client
      .get()
      .uri(properties.baseUrl() + "/banks/v1/{code}", code)
      .accept(APPLICATION_JSON)
      .retrieve()
      .onStatus(
        status -> status == NOT_FOUND,
        response -> { throw new InvalidBankCodeException(); }
      )
      .bodyToMono(BankResponse.class)
      .block()
      .fullName();
  }

}
