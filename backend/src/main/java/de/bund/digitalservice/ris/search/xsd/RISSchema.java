package de.bund.digitalservice.ris.search.xsd;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a schema field or parameter whose OpenAPI description should be looked up from the XSD
 * documentation (see {@link XSDDescriptionParser}), falling back to {@link #description()} when no
 * matching XSD text is found.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Inherited
@Schema
public @interface RISSchema {

  /** The XSD element/type key (e.g. "ris:dokumentnummer") to look up the description for. */
  String name();

  /** Fallback-Beschreibung, falls kein XSD-Text gefunden wird (optional). */
  String description() default "";

  /** Sprache des XSD-Textes (normalisiert auf Basissprache, z. B. "de"). */
  String language() default "de";

  /** Example value shown in the generated OpenAPI schema. */
  String example() default "";

  /** Whether this property is required in the generated OpenAPI schema. */
  RequiredMode requiredMode() default RequiredMode.AUTO;
}
