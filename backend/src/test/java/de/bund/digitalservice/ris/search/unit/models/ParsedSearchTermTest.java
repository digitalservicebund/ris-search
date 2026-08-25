package de.bund.digitalservice.ris.search.unit.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.bund.digitalservice.ris.search.exception.OpenSearchTermLimitExceeded;
import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ParsedSearchTermTest {

  @Test
  void ItThrowsAnExceptionWhenUnquotedTokenLimitIsExceeded() {
    assertThatThrownBy(
            () -> {
              new ParsedSearchTerm(
                  "query", Collections.nCopies(20, "token"), Collections.nCopies(16, "token"));
            })
        .isInstanceOfSatisfying(
            OpenSearchTermLimitExceeded.class,
            ex -> {
              assertThat(ex.getLimit()).isEqualTo(35);
              assertThat(ex.getActualTermCount()).isEqualTo(36);
            });
  }
}
