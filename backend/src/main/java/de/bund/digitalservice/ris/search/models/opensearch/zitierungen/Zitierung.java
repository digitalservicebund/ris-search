package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import de.bund.digitalservice.ris.search.models.opensearch.common.SearchTextConvertible;

/** A citation of or by a case law document, in either {@link Richtung}. */
public interface Zitierung extends SearchTextConvertible {

  Richtung richtung();
}
