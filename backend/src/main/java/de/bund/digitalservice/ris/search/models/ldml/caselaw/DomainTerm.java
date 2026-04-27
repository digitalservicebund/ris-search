package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/** Abstract base class for the "Motivation" phase of a legal document. * */
@XmlEnum
public enum DomainTerm {
  @XmlEnumValue("Entscheidungsgründe")
  DECISION_GROUNDS,

  @XmlEnumValue("Gründe")
  GROUNDS,

  @XmlEnumValue("Sonstiger Langtext")
  OTHER_LONGTEXT,

  @XmlEnumValue("Abweichende Meinung")
  DISSENTING_OPINION,

  /**
   * Represents a "Leitsatz" (Guiding Principle) section.
   *
   * <p>These are the official core legal statements formulated by the court.
   */
  @XmlEnumValue("Leitsatz")
  GUIDING_PRINCIPLE,

  /**
   * Represents a "Gliederung" (Outline or Table of Contents) section.
   *
   * <p>Used for structured overviews in particularly long or complex court decisions.
   */
  @XmlEnumValue("Gliederung")
  OUTLINE,
}
