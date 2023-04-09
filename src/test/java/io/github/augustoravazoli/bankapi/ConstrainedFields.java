package io.github.augustoravazoli.bankapi;

import org.springframework.restdocs.payload.FieldDescriptor;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

public class ConstrainedFields {

  private final ConstraintDescriptions constraints;

  public ConstrainedFields(Class<?> clazz) {
    this.constraints = new ConstraintDescriptions(clazz);
  }

  public FieldDescriptor withPath(String path) {
    var description = collectionToDelimitedString(constraints.descriptionsForProperty(path), ". ");
    return fieldWithPath(path).attributes(key("constraints").value(description));
  }

}
