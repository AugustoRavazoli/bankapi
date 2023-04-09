package io.github.augustoravazoli.bankapi.customer;

import java.util.regex.Pattern;
import static org.hamcrest.Matchers.endsWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import io.github.augustoravazoli.bankapi.ConstrainedFields;
import io.github.augustoravazoli.bankapi.GlobalExceptionHandler.ErrorResponse;

@Import(CustomerMapperImpl.class)
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

  @MockBean
  private CustomerService customerService;

  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @BeforeEach
  void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
    var cpfPattern = Pattern.compile("\\\"((\\d{11})|((\\d{3}[-.\\/]){3}\\d{2}))\\\"", Pattern.MULTILINE);
    var replacement = "xxx.xxx.xxx-xx";
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(documentationConfiguration(provider)
        .operationPreprocessors()
        .withRequestDefaults(prettyPrint(), replacePattern(cpfPattern, replacement))
        .withResponseDefaults(prettyPrint(), replacePattern(cpfPattern, replacement))
      )
      .build();
  }

  @Test
  void whenCreateCustomer_thenReturns201AndCustomer() throws Exception {
    var newCustomer = CustomerFactory.createRequest();
    var savedCustomer = CustomerFactory.createEntity();
    var returnedCustomer = CustomerFactory.createResponse();
    when(customerService.createCustomer(any(Customer.class))).thenReturn(savedCustomer);
    mvc.perform(post("/api/v1/customers")
      .content(mapper.writeValueAsString(newCustomer))
      .contentType(APPLICATION_JSON)
    )
    .andExpectAll(
      status().isCreated(),
      header().string(LOCATION, endsWith("/api/v1/customers/" + returnedCustomer.id())),
      content().json(mapper.writeValueAsString(returnedCustomer)) 
    )
    .andDo(document("customer/create", customerSnippet()));
  }

  @Test
  void givenInvalidRequest_whenCreateCustomer_thenReturns422AndErrorInfo() throws Exception {
    var invalidCustomer = CustomerFactory.createRequestMissingName();
    var errorInfo = new ErrorResponse("validation errors");
    mvc.perform(post("/api/v1/customers")
      .content(mapper.writeValueAsString(invalidCustomer))
      .contentType(APPLICATION_JSON)
    )
    .andExpectAll(
      status().isUnprocessableEntity(),
      content().json(mapper.writeValueAsString(errorInfo))
    )
    .andDo(document("customer/error"));
  }

  private RequestFieldsSnippet customerSnippet() {
    var constrainedFields = new ConstrainedFields(CustomerRequest.class);
    return requestFields(
      constrainedFields.withPath("name").description("Customer's name"),
      constrainedFields.withPath("email").description("Customer's email"),
      constrainedFields.withPath("cpf").description("Customer's CPF"),
      constrainedFields.withPath("birthDate").description("Customer's date of birth")
    );
  }

}
