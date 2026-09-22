package de.bund.digitalservice.ris.search.models.api.parameters;

import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.time.LocalDate;
import lombok.Getter;

/** Parameter class to be used with the norm endpoints. */
@Getter
public class NormExpressionId extends NormWorkId {

  @Parameter(in = ParameterIn.PATH, example = "2020-06-19")
  private final LocalDate pointInTime;

  @Parameter(in = ParameterIn.PATH, example = "2")
  private final Integer version;

  @Parameter(in = ParameterIn.PATH, example = "deu")
  private final String language;

  /**
   * Constructor for a NormExpressionId
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   * @param pointInTime the point in time representing the start of the validity of the document
   * @param version the version of the document
   * @param language the language of the document
   */
  public NormExpressionId(
      String jurisdiction,
      String agent,
      String year,
      String naturalIdentifier,
      LocalDate pointInTime,
      Integer version,
      String language) {
    super(jurisdiction, agent, year, naturalIdentifier);
    this.pointInTime = pointInTime;
    this.version = version;
    this.language = language;
  }

  /**
   * @return this object converted to an ExpressionEli
   */
  public ExpressionEli toExpressionEli() {
    return new ExpressionEli(
        jurisdiction, agent, year, naturalIdentifier, pointInTime, version, language);
  }
}
