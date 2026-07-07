package de.bund.digitalservice.ris.search.xsd;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Inherited
@Schema
public @interface RISSchema {

  String name();

  /**
   * Fallback-Beschreibung, falls kein XSD-Text gefunden wird (optional).
   */
  String description() default "";

  /**
   * Sprache des XSD-Textes (normalisiert auf Basissprache, z. B. "de").
   */
  String language() default "de";

  String example() default "";

  RequiredMode requiredMode() default RequiredMode.AUTO;
}

