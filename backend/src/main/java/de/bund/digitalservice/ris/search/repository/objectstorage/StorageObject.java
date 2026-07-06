package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.util.Optional;

/**
 * Represents an object retrieved from the ObjectStorage.
 *
 * @param key unique identifier of the object
 * @param bytes content of the object
 */
public record StorageObject(String key, Optional<byte[]> bytes) {}
