package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

/**
 * The AccessRights class serves as a container for predefined access rights constants.
 *
 * <p>This class is designed to offer standardized values that represent different levels of access
 * control within the system. It is not intended to be instantiated.
 *
 * <p>The constants provided by this class can be used in various contexts, such as metadata or
 * configuration settings, to denote the access level of a given entity.
 *
 * <p>The current implementation includes the following access right: - "public": Represents a
 * publicly accessible resource.
 */
public class AccessRights {
  private AccessRights() {}

  public static final String PUBLIC = "public";
}
