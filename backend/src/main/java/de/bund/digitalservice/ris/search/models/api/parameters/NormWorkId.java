package de.bund.digitalservice.ris.search.models.api.parameters;

import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/** Parameter class to be used with the norm endpoints. */
@Getter
public class NormWorkId {
  public static final String BUND_DESCRIPTION = "Country or regional code for the jurisdiction";
  public static final String BUND_EXAMPLE = "bund";
  public static final String AGENT_DESCRIPTION =
      "Agent or authority issuing the legislation, e.g., 'bgbl-1' for Bundesgesetzblatt Teil I (Federal Law Gazette part I)";
  public static final String AGENT_EXAMPLE = "bgbl-1";
  public static final String YEAR_DESCRIPTION = "Year the legislation was enacted or published";
  public static final String YEAR_EXAMPLE = "1979";
  public static final String NATURAL_IDENTIFIER_DESCRIPTION =
      "Unique natural identifier for the legislation, specific to the jurisdiction and agent";
  public static final String NATURAL_IDENTIFIER_EXAMPLE = "s1325";

  @Parameter(
      in = ParameterIn.PATH,
      description = BUND_DESCRIPTION,
      schema = @Schema(allowableValues = {BUND_EXAMPLE}))
  protected final String jurisdiction;

  @Parameter(in = ParameterIn.PATH, description = AGENT_DESCRIPTION, example = AGENT_EXAMPLE)
  protected final String agent;

  @Parameter(in = ParameterIn.PATH, description = YEAR_DESCRIPTION, example = YEAR_EXAMPLE)
  protected final String year;

  @Parameter(
      in = ParameterIn.PATH,
      description = NATURAL_IDENTIFIER_DESCRIPTION,
      example = NATURAL_IDENTIFIER_EXAMPLE)
  protected final String naturalIdentifier;

  /**
   * Constructor for a NormWorkId
   *
   * @param jurisdiction the jurisdiction to which the legal document belongs
   * @param agent the agent responsible for the legal document
   * @param year the year of issuance for the legal document
   * @param naturalIdentifier an identifier for the legal document
   */
  public NormWorkId(String jurisdiction, String agent, String year, String naturalIdentifier) {
    this.jurisdiction = jurisdiction;
    this.agent = agent;
    this.year = year;
    this.naturalIdentifier = naturalIdentifier;
  }

  /**
   * @return this object converted to a WorkEli
   */
  public WorkEli toWorkEli() {
    return new WorkEli(jurisdiction, agent, year, naturalIdentifier);
  }
}
