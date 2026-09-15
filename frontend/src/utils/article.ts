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
