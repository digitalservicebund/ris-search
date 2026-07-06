package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.util.Optional;

public record DocumentObject(String key, Optional<byte[]> bytes) {}
