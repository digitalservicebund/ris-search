package de.bund.digitalservice.ris.search.models;

/** Represents the publication status of an entity. */
public enum PublicationStatus {
  /** Indicates that the entity is unpublished. */
  UNPUBLISHED,

  /** Indicates that the entity is published. */
  PUBLISHED,

  /** Indicates that the entity is in the process of being published. */
  PUBLISHING,

  /** Indicates that the entity is in the process of being deleted. */
  DELETING,

  /** Indicates that the entity is locked. */
  LOCKED,

  /** Indicates that the entity is duplicated. */
  DUPLICATED,

  /** Indicates that the publication status of the entity is unknown. */
  UNKNOWN
}
