package de.bund.digitalservice.ris.search.xsd;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.models.media.Schema;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.springdoc.core.customizers.PropertyCustomizer;

public class XSDPropertyCustomizer implements PropertyCustomizer {
  private final XSDDescriptionParser parser;

  public XSDPropertyCustomizer(XSDDescriptionParser parser) {
    this.parser = parser;
  }

  @Override
  public Schema<?> customize(Schema property, AnnotatedType type) {
    if (type.getCtxAnnotations() == null) {
      return property;
    }

    RISSchema myAnno = Arrays.stream(type.getCtxAnnotations())
        .filter(RISSchema.class::isInstance)
        .map(RISSchema.class::cast)
        .findFirst()
        .orElse(null);

    if (myAnno == null) {
      return property;
    }

    io.swagger.v3.oas.annotations.media.Schema schemaProxy = (io.swagger.v3.oas.annotations.media.Schema) Proxy.newProxyInstance(
        io.swagger.v3.oas.annotations.media.Schema.class.getClassLoader(),
        new Class<?>[]{io.swagger.v3.oas.annotations.media.Schema.class},
        (proxy, method, args) -> {
          String methodName = method.getName();

          if ("annotationType".equals(methodName)) {
            return io.swagger.v3.oas.annotations.media.Schema.class;
          }

          if ("description".equals(methodName)) {
            return parser.findDescription(myAnno.name(), myAnno.language())
                .orElse(myAnno.description());
          }

          try {
            Method customMethod = RISSchema.class.getMethod(methodName, method.getParameterTypes());
            return customMethod.invoke(myAnno);
          } catch (NoSuchMethodException e) {
            return method.getDefaultValue();
          }
        }
    );

    AnnotationsUtils.getSchemaFromAnnotation(schemaProxy, null)
        .ifPresent(parsedSchema ->
          mergeNonNullProperties(parsedSchema, property)
        );

    return property;
  }

  private void mergeNonNullProperties(Schema<?> source, Schema<?> target) {
    for (Method method : Schema.class.getMethods()) {
      if (method.getName().startsWith("get") && method.getParameterCount() == 0 && !method.getName().equals("getClass")) {
        try {
          Object value = method.invoke(source);
          // Nur überschreiben, wenn Swagger aus der Annotation einen echten Wert extrahiert hat
          if (value != null) {
            String setterName = "set" + method.getName().substring(3);
            Method setter = Schema.class.getMethod(setterName, method.getReturnType());
            setter.invoke(target, value);
          }
        } catch (Exception ignored) {
          // Setzer-Abweichungen oder fehlende Methoden ignorieren
        }
      }
    }
  }
}
