package de.bund.digitalservice.ris.search.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** JsonldType annotation to pick up type name in openApi schema generation */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonldType {
  /**
   * @return jsonLd Type name
   */
  public String value() default "";
}
