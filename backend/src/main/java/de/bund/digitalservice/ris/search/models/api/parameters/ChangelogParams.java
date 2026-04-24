package de.bund.digitalservice.ris.search.models.api.parameters;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class ChangelogParams {
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  OffsetDateTime from;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  OffsetDateTime to;
}
