package io.github.augustoravazoli.bankapi.customer;

import java.time.LocalDate;
import static org.hamcrest.Matchers.is;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import jakarta.validation.constraints.Null;
import io.github.augustoravazoli.bankapi.ControllerTestTemplate;

@Import(CustomerMapperImpl.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest extends ControllerTestTemplate {

  @MockBean
  private CustomerService customerService;

  @Test
  void whenCreateCustomer_thenReturns201AndCreatedCustomer() throws Exception {
    // given
    var newCustomer = new CustomerRequest(
      "customer",  "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    var savedCustomer = new Customer(
      1L, "customer", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    var returnedCustomer = new CustomerResponse(
      1L, "customer", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    // and
    when(customerService.createCustomer(any(Customer.class))).thenReturn(savedCustomer);
    // when
    mvc.perform(post("/api/v1/customers")
      .content(toJson(newCustomer))
      .contentType(APPLICATION_JSON)
    )
    // then
    .andExpectAll(
      status().isCreated(),
      header().string(LOCATION, endsWith("/api/v1/customers/" + returnedCustomer.id())),
      content().json(toJson(returnedCustomer)) 
    )
    .andDo(document("customer/create", customerSnippet()));
  }

  @Test
  void givenInvalidRequest_whenCreateCustomer_thenReturns422AndErrorInfo() throws Exception {
    // given
    var invalidCustomer = new CustomerRequest(
      "", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    // when
    mvc.perform(post("/api/v1/customers")
      .content(toJson(invalidCustomer))
      .contentType(APPLICATION_JSON)
    )
    // then
    .andExpectAll(
      status().isUnprocessableEntity(),
      jsonPath("$.message", is("validation errors on your request body"))
    )
    .andDo(document("customer/error"));
  }

  @Test
  void whenFindCustomer_thenReturns200AndFindedCustomer() throws Exception {
    // given
    var findedCustomer = new Customer(
      1L, "customer", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    var returnedCustomer = new CustomerResponse(
      1L, "customer", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    // and
    when(customerService.findCustomer(anyString())).thenReturn(findedCustomer);
    // when
    mvc.perform(
      get("/api/v1/customers/{cpf}", returnedCustomer.cpf())
    )
    // then
    .andExpectAll(
      status().isOk(),
      content().json(toJson(returnedCustomer))
    )
    .andDo(document("customer/find"));
  }

  @Test
  void givenInvalidCpf_whenFindCustomer_thenReturns422AndErrorInfo() throws Exception {
    // given
    var cpf = "000.000.000-00";
    // when
    mvc.perform(
      get("/api/v1/customers/{cpf}", cpf)
    )
    // then
    .andExpectAll(
      status().isUnprocessableEntity(),
      jsonPath("$.message", is("validation errors on your request query parameters"))
    );
  }

  @Test
  void whenEditCustomer_thenReturns200AndEditedCustomer() throws Exception {
    // given
    var customer = new CustomerRequest(
      "customer", "customer@example.com", null, LocalDate.of(1990, 9, 9)
    );
    var editedCustomer = new Customer(
      1L, "customer", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    var returnedCustomer = new CustomerResponse(
      1L, "customer", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    // and
    when(customerService.editCustomer(anyString(), any(Customer.class))).thenReturn(editedCustomer);
    // when
    mvc.perform(put("/api/v1/customers/{cpf}", returnedCustomer.cpf())
      .contentType(APPLICATION_JSON)
      .content(toJson(customer))
    )
    // then
    .andExpectAll(
      status().isOk(),
      content().json(toJson(returnedCustomer))
    )
    .andDo(document("customer/edit"));
  }

  @Test
  void whenRemoveCustomer_thenReturns204() throws Exception {
    // given
    var customer = new Customer(
      1L, "customer", "customer@example.com", CPF, LocalDate.of(1990, 9, 9)
    );
    // and
    doNothing().when(customerService).removeCustomer(anyString());
    // when
    mvc.perform(
      delete("/api/v1/customers/{cpf}", customer.getCpf())
    )
    // then
    .andExpectAll(
      status().isNoContent(),
      jsonPath("$").doesNotExist()
    )
    .andDo(document("customer/remove"));
  }

  private RequestFieldsSnippet customerSnippet() {
    var fields = new ConstrainedFields(CustomerRequest.class);
    return requestFields(
      fields.path("name").description("Customer's name"),
      fields.path("email").description("Customer's email"),
      fields.path("cpf", Null.class).description("Customer's CPF"),
      fields.path("birthDate").description("Customer's date of birth")
    );
  }

}
