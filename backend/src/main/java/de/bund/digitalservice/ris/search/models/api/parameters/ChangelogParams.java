package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class ChangelogParams {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @NotNull
  OffsetDateTime from;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @NotNull
  OffsetDateTime to;
}
