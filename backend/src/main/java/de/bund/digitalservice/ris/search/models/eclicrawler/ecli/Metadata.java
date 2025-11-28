package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the metadata information of a resource, encapsulating various attributes such as
 * identifier, versioning, creator, coverage, and other descriptive properties.
 *
 * <p>This class is used as part of the ECLI metadata structure, supporting XML serialization and
 * deserialization through JAXB annotations. Each field in this class represents a specific aspect
 * of the metadata, conforming to the required standards.
 *
 * <p>Fields: - `identifier`: The unique identifier associated with the resource. - `isVersionOf`:
 * Information about prior versions of the resource. - `creator`: Details about the creator of the
 * resource. - `coverage`: Geographical or jurisdictional coverage of the resource. - `date`:
 * Publication or creation date of the resource. - `language`: The language of the resource. -
 * `publisher`: Information about the entity responsible for publishing the resource. -
 * `accessRights`: Details about access rights to the resource, defaulting to "public". - `type`:
 * The classification or type of the resource.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Metadata {
  private Identifier identifier;

  private IsVersionOf isVersionOf;

  private Creator creator;

  private Coverage coverage;

  @XmlElement private String date;

  private Language language;

  private Publisher publisher;

  @XmlElement private String accessRights = "public";

  private Type type;
}
