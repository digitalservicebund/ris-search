import _ from "lodash";
import type { LegislationWork, SearchResult } from "~/types";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

export function getMostRelevantExpression(
  list: SearchResult<LegislationWork>[],
): string | null | undefined {
  if (list.length === 0) {
    return null;
  }
  const expressions = list.map((member) => member.item.workExample);
  const activeExpressions = expressions.filter(
    (expression) => expression.legislationLegalForce === "InForce",
  );
  if (activeExpressions.length > 0) {
    if (activeExpressions.length > 1) {
      console.info(
        "found more than one matching active expressions",
        activeExpressions,
      );
    }
    return activeExpressions[0]?.legislationIdentifier;
  }
  const referenceDate = getCurrentDateInGermanyFormatted();
  const [future, past] = _.partition(
    expressions,
    (item) => item.temporalCoverage >= referenceDate,
  );
  if (future.length > 0) {
    return _.sortBy(future, "legislationLegalForce")[0]?.legislationIdentifier;
  }
  if (past.length > 0) {
    return (
      _.sortBy(past, "legislationLegalForce").at(-1)?.legislationIdentifier ??
      null
    );
  }
  throw new Error("Could not identify the most relevant expression");
}
