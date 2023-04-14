package io.github.augustoravazoli.bankapi;

import java.util.Arrays;
import static java.util.stream.Collectors.joining;
import java.net.URI;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;

import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(RestDocumentationExtension.class)
public abstract class ControllerTestTemplate {

  protected static final String CPF = new Faker().cpf().valid();

  private static final OperationPreprocessor[] preprocessors = {
    modifyUris().host("example.com").removePort(),
    modifyHeaders().remove("Content-Disposition"),
    prettyPrint(),
    hideCpf()
  };

  protected MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @BeforeEach
  protected final void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(documentationConfiguration(provider)
        .operationPreprocessors()
        .withRequestDefaults(preprocessors)
        .withResponseDefaults(preprocessors)
      )
      .build();
  }

  protected final <T> String toJson(T object) throws JsonProcessingException {
    return mapper.writeValueAsString(object);
  }

  public static class ConstrainedFields {

    private final Class<?> clazz;

    public ConstrainedFields(Class<?> clazz) {
      this.clazz = clazz;
    }

    public FieldDescriptor path(String path, Class<?>... hiddenConstraints) {
      var constraints = new ConstraintDescriptions(clazz, fieldResolver(hiddenConstraints));
      var description = constraints.descriptionsForProperty(path).stream()
        .filter(s -> !s.isEmpty())
        .collect(joining(". "));
      return fieldWithPath(path).attributes(key("constraints").value(description));
    }

    private ConstraintDescriptionResolver fieldResolver(Class<?>... hiddenConstraints) {
      var resolver = new ResourceBundleConstraintDescriptionResolver();
      return constraint -> {
        var constraintExists = Arrays.stream(hiddenConstraints)
          .filter(hc -> hc.getName().equals(constraint.getName()))
          .findFirst()
          .isPresent();
        return constraintExists ? "" : resolver.resolveDescription(constraint);
      };
    }

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
