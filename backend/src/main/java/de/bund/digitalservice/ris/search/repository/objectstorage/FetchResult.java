package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.util.Optional;

public record FetchResult(String key, Optional<byte[]> bytes) {}
