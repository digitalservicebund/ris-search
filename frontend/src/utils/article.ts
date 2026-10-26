import type { LegislationExpression } from "~/types/api";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";

export function tocHeadlineAdditionLabel(temporalCoverage: string | undefined) {
  const coverage = temporalCoverageToValidityInterval(temporalCoverage);
  const from = dateFormattedDDMMYYYY(coverage?.from);
  const to = dateFormattedDDMMYYYY(coverage?.to);

  if (from && to) {
    return `(${from} - ${to})`;
  } else if (from) {
    return `(vom ${from})`;
  } else if (to) {
    return `(bis ${to})`;
  } else {
    return "";
  }
}

function validFrom(expression: LegislationExpression) {
  return getVersionValidFrom(expression)?.valueOf() ?? -Infinity;
}

/** Picks the expression whose validity period starts last. */
export function getNewestExpression(
  expressions: LegislationExpression[],
): LegislationExpression | undefined {
  const [newest] = expressions.toSorted((a, b) => validFrom(b) - validFrom(a));
  return newest;
}
