package io.github.augustoravazoli.bankapi.account;

import java.io.IOException;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class BankClientTest {

  public static MockWebServer mockedServer;
  private BankClient bankClient;

  @BeforeAll
  static void setUp() throws IOException {
    mockedServer = new MockWebServer();
    mockedServer.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockedServer.shutdown();
  }

  @BeforeEach
  void initialize() {
    var url = mockedServer.url("/").url().toString();
    var properties = new BankClientProperties(url);
    bankClient = new BankClient(WebClient.create(), properties);
  }

  @AfterEach
  void resetEnvironment() throws InterruptedException {
    mockedServer.takeRequest(1000, MILLISECONDS);
  }

  @Test
  void whenFindBankNameByCode_thenReturnsBankName() throws Exception {
    // given
    var code = 1;
    // and
    var json = "{"
      + "\"isbp\":\"xxxxxxxx\","
      + "\"fullName\":\"bankname\","
      + "\"code\":1"
      + "}";
    mockedServer.enqueue(new MockResponse()
      .setBody(json)
      .addHeader("Content-Type", "application/json")
    );
    // when
    var bankName = bankClient.findBankNameByCode(code);
    var recordedRequest = mockedServer.takeRequest();
    // then
    assertThat(bankName).isEqualTo("bankname");
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    assertThat(recordedRequest.getPath()).isEqualTo("/api/banks/v1/" + code);
  }

  @Test
  void givenInvalidBankCode_whenFindBankNameByCode_thenThrowsInvalidBankCodeException() throws Exception {
    // given
    var code = -1;
    // and
    mockedServer.enqueue(new MockResponse()
      .setResponseCode(404)
      .addHeader("Content-Type", "application/json")
    );
    // then
    assertThatThrownBy(() -> bankClient.findBankNameByCode(code))
      .isInstanceOf(InvalidBankCodeException.class);
  }

}
