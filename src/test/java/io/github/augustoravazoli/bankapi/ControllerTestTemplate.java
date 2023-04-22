package io.github.augustoravazoli.bankapi;

import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.joining;
import java.net.URI;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import org.springframework.restdocs.constraints.Constraint;
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
    modifyHeaders().remove("Content-Disposition").remove("Vary"),
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

    private final ConstraintDescriptions constraints;
    private final List<Class<?>> groups;

    public ConstrainedFields(Class<?> clazz) {
      groups = new ArrayList<>();
      constraints = new ConstraintDescriptions(clazz, resolver(groups));
    }

    public FieldDescriptor pathExcludingGroups(String path, Class<?>... excludedGroups) {
      groups.addAll(List.of(excludedGroups));
      return path(path);
    }

    public FieldDescriptor path(String path) {
      var description = constraints.descriptionsForProperty(path).stream()
        .filter(s -> !s.isEmpty())
        .collect(joining(". "));
      return fieldWithPath(path).attributes(key("constraints").value(description));
    }

    private static ResourceBundleConstraintDescriptionResolver resolver(List<Class<?>> groups) {
      return new ResourceBundleConstraintDescriptionResolver() {

        @Override
        public String resolveDescription(Constraint constraint) {
          var description = super.resolveDescription(constraint);
          if (!groups.isEmpty()) {
            var constraintGroups = (Class<?>[]) constraint.getConfiguration().get("groups");
            if (List.of(constraintGroups).containsAll(groups)) {
              description = "";
              groups.clear();
            }
          }
          return description;
        }

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
        return new OperationResponseFactory().create(
          response.getStatus(),
          replaceCpf(response.getHeaders()),
          replaceCpf(response.getContentAsString())
        );
      }

      private HttpHeaders replaceCpf(HttpHeaders headers) {
        if (headers.getLocation() != null) {
          var path = headers.getLocation().toASCIIString().replaceAll(CPF_PATTERN, REPLACEMENT);
          var newHeaders = new HttpHeaders();
          newHeaders.addAll(headers);
          newHeaders.setLocation(URI.create(path));
          return newHeaders;
        }
        return headers;
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
