import _ from "lodash";
import { getMostRelevantExpression } from "~/pages/norms/eli/[jurisdiction]/[agent]/[year]/[naturalIdentifier]/index.logic";
import type {
  LegislationExpression,
  LegislationWork,
  SearchResult,
} from "~/types";

vi.mock("~/utils/dateFormatting", () => ({
  getCurrentDateInGermanyFormatted: vi.fn().mockReturnValue("2000-01-01"),
}));

type PartialExpression = Pick<
  LegislationExpression,
  "legislationLegalForce" | "temporalCoverage" | "legislationIdentifier"
>;

describe("getMostRelevantExpression", () => {
  const currentExpression: PartialExpression = {
    legislationLegalForce: "InForce",
    temporalCoverage: "1999-01-01/..",
    legislationIdentifier: "currentExpression",
  };
  const veryOldExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "1871-05-15/1871-05-17",
    legislationIdentifier: "veryOldExpression",
  };
  const oldExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "1900-01-01/1980-01-01",
    legislationIdentifier: "oldExpression",
  };
  const upcomingExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "2001-01-01/..",
    legislationIdentifier: "upcomingExpression",
  };
  const farFutureExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "3000-01-01/..",
    legislationIdentifier: "farFutureExpression",
  };

  function transform(
    partialExpressions: PartialExpression[],
  ): SearchResult<LegislationWork>[] {
    return partialExpressions.map(
      (partialExpression) =>
        ({
          item: {
            workExample: partialExpression as LegislationExpression,
          },
        }) as SearchResult<LegislationWork>,
    );
  }

  const allExpressions = [
    veryOldExpression,
    oldExpression,
    currentExpression,
    upcomingExpression,
    farFutureExpression,
  ];

  it("picks a current expression if available", () => {
    const testCase = transform(allExpressions);
    expect(getMostRelevantExpression(testCase)).toBe("currentExpression");
  });

  it("picks the nearest future expression if there is no current expression", () => {
    const testCase = transform(_.without(allExpressions, currentExpression));
    expect(getMostRelevantExpression(testCase)).toBe("upcomingExpression");
  });

  it("picks the most recent past expression if there is no current or future expression", () => {
    const testCase = transform([veryOldExpression, oldExpression]);
    expect(getMostRelevantExpression(testCase)).toBe("oldExpression");
  });
});
