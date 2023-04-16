package io.github.augustoravazoli.bankapi.account;

import java.time.LocalDate;
import java.math.BigDecimal;
import static org.hamcrest.Matchers.endsWith;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import io.github.augustoravazoli.bankapi.ControllerTestTemplate;
import io.github.augustoravazoli.bankapi.customer.Customer;

@Import(AccountMapperImpl.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest extends ControllerTestTemplate {

  @MockBean
  private AccountService accountService;

  @Test
  void whenCreateAccount_thenReturns201AndCreatedAccount() throws Exception {
    // given
    var newAccount = new AccountRequest(1);
    var savedAccount = new Account(1L, "bankname", new Customer());
    var returnedAccount = new AccountResponse(1L, "bankname", BigDecimal.ZERO, LocalDate.now());
    // and
    when(accountService.createAccount(anyString(), any(AccountRequest.class))).thenReturn(savedAccount);
    // when
    mvc.perform(post("/api/v1/customers/{cpf}/accounts", CPF)
      .content(toJson(newAccount))
      .contentType(APPLICATION_JSON)
    )
    // then
    .andExpectAll(
      status().isCreated(),
      header().string(LOCATION, endsWith("/api/v1/customers/" + CPF + "/accounts/" + returnedAccount.id())),
      content().json(toJson(returnedAccount))
    )
    .andDo(document("account/create", accountSnippet()));
  }

  @Test
  void whenFindAccount_thenReturns200AndFindedAccount() throws Exception {
    // given
    var findedAccount = new Account(1L, "bankname", new Customer());
    var returnedAccount = new AccountResponse(1L, "bankname", BigDecimal.ZERO, LocalDate.now());
    // and
    when(accountService.findAccount(anyString(), anyLong())).thenReturn(findedAccount);
    // when
    mvc.perform(
      get("/api/v1/customers/{cpf}/accounts/{id}", CPF, returnedAccount.id())
    )
    // then
    .andExpectAll(
      status().isOk(),
      content().json(toJson(returnedAccount))
    )
    .andDo(document("account/find"));
  }

  private RequestFieldsSnippet accountSnippet() {
    var fields = new ConstrainedFields(AccountRequest.class);
    return requestFields(
      fields.path("bankCode").description("Account's bank code")
    );
  }

}
