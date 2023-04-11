package io.github.augustoravazoli.bankapi;

import org.springframework.restdocs.payload.FieldDescriptor;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;

public class ConstrainedFields {

  private final Class<?> clazz;

  public ConstrainedFields(Class<?> clazz) {
    this.clazz = clazz;
  }

  public FieldDescriptor withPath(String path) {
    var constraints = new ConstraintDescriptions(clazz);
    return buildFieldDescriptor(path, constraints);
  }

  public FieldDescriptor withPathHiddenConstraints(String path, Class<?>... hiddenConstraints) {
    var constraints = new ConstraintDescriptions(clazz, fieldResolver(hiddenConstraints));
    return buildFieldDescriptor(path, constraints);
  }

  private FieldDescriptor buildFieldDescriptor(String path, ConstraintDescriptions constraints) {
    var description = String.join(". ", constraints.descriptionsForProperty(path));
    if (description.charAt(0) == '.') {
      description = description.substring(1);
    }
    return fieldWithPath(path).attributes(key("constraints").value(description));
  }

  private ConstraintDescriptionResolver fieldResolver(Class<?>... hiddenConstraints) {
    return new ConstraintDescriptionResolver() {

      private final ResourceBundleConstraintDescriptionResolver defaultResolver;

      {
        defaultResolver = new ResourceBundleConstraintDescriptionResolver();
      }

      @Override
      public String resolveDescription(Constraint constraint) {
        for (var hc : hiddenConstraints) {
          if (constraint.getName().equals(hc.getName())) {
            return "";
          }
        }
        return defaultResolver.resolveDescription(constraint);
      }

    };
  }

}
