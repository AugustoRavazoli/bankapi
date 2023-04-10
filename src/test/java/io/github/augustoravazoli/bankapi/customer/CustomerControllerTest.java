package io.github.augustoravazoli.bankapi.customer;

import java.net.URI;
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
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    var preprocessors = new OperationPreprocessor[] {
      modifyUris().host("example.com").removePort(),
      prettyPrint(),
      hideCpf()
    };
    mvc = MockMvcBuilders
      .webAppContextSetup(context)
      .apply(documentationConfiguration(provider)
        .operationPreprocessors()
        .withRequestDefaults(preprocessors)
        .withResponseDefaults(preprocessors)
      )
      .alwaysDo(print())
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
    var errorInfo = new ErrorResponse("validation errors on your request body");
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

  @Test
  void whenFindCustomer_thenReturns200AndCustomer() throws Exception {
    var findedCustomer = CustomerFactory.createEntity();
    var returnedCustomer = CustomerFactory.createResponse();
    when(customerService.findCustomer(anyString())).thenReturn(findedCustomer);
    mvc.perform(get("/api/v1/customers/{cpf}", findedCustomer.getCpf()))
      .andExpectAll(
        status().isOk(),
        content().json(mapper.writeValueAsString(returnedCustomer))
      )
      .andDo(document("customer/find"));
  }

  @Test
  void givenInvalidCpf_whenFindCustomer_thenReturns422AndErrorInfo() throws Exception {
    var errorInfo = new ErrorResponse("validation errors on your request query parameters");
    mvc.perform(get("/api/v1/customers/{cpf}", "000.000.000-00"))
      .andExpectAll(
        status().isUnprocessableEntity(),
        content().json(mapper.writeValueAsString(errorInfo))
      );
  }

  @Test
  void whenEditCustomer_thenReturns200AndCustomer() throws Exception {
    var customer = CustomerFactory.createRequestMissingCpf();
    var editedCustomer = CustomerFactory.createEntity();
    var returnedCustomer = CustomerFactory.createResponse();
    when(customerService.editCustomer(anyString(), any(Customer.class))).thenReturn(editedCustomer);
    mvc.perform(put("/api/v1/customers/{cpf}", editedCustomer.getCpf())
      .contentType(APPLICATION_JSON)
      .content(mapper.writeValueAsString(customer))
    )
    .andExpectAll(
      status().isOk(),
      content().json(mapper.writeValueAsString(returnedCustomer))
    )
    .andDo(document("customer/edit"));
  }

  @Test
  void whenRemoveCustomer_thenReturns204() throws Exception {
    var customer = CustomerFactory.createEntity();
    doNothing().when(customerService).removeCustomer(anyString());
    mvc.perform(delete("/api/v1/customers/{cpf}", customer.getCpf()))
      .andExpectAll(
        status().isNoContent(),
        content().string("")
      )
      .andDo(document("customer/remove"));
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

  private static OperationPreprocessor hideCpf() {
    return new OperationPreprocessor() {

      private static final String CPF_PATTERN = "((\\d{11})|((\\d{3}[-.\\/]){3}\\d{2}))";
      private static final String REPLACEMENT = "xxx.xxx.xxx-xx";

      @Override
      public OperationRequest preprocess(OperationRequest request) {
        return new OperationRequestFactory().create(
          replaceCpf(request.getUri()),
          request.getMethod(),
          replaceCpf(request.getContentAsString()),
          request.getHeaders(),
          request.getParts(),
          request.getCookies()
        );
      }

      @Override
      public OperationResponse preprocess(OperationResponse response) {
        return new OperationResponseFactory().createFrom(
          response,
          replaceCpf(response.getContentAsString())
        );
      }

      private URI replaceCpf(URI uri) {
        var path = uri.toString().replaceAll(CPF_PATTERN, REPLACEMENT);
        return URI.create(path);
      }

      private byte[] replaceCpf(String content) {
        return content.replaceAll(CPF_PATTERN, REPLACEMENT).getBytes();
      }

    };
  }

}
