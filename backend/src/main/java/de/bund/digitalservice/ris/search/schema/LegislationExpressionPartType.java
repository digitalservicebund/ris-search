package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonValue;

/** Possible types the part of a LegislationPart can be mapped to */
public enum LegislationExpressionPartType {
  PREAMBLE("preamble"),
  ARTICLE("article"),
  CONCLUSION("conclusion"),
  ATTACHMENT("attachment");

  private final String val;

  LegislationExpressionPartType(String val) {
    this.val = val;
  }

  @JsonValue()
  public String getVal() {
    return val;
  }
}
