package de.bund.digitalservice.ris.search.unit.client;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClientDummy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostHogClientDummyTest {

  @Test
  void throwsUnsupportedOperationException() {
    var postHogClient = new PostHogClientDummy();

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(
            () -> {
              postHogClient.submitFeedback("", "", "", "");
            });
  }
}
