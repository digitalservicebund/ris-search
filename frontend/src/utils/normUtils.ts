import { formattedDate } from "~/utils/dateFormatting";

export interface ValidityInterval {
  from?: string;
  to?: string;
}

export function temporalCoverageToValidityInterval(
  temporalCoverage: string | null | undefined,
): ValidityInterval | undefined {
  if (!temporalCoverage) return undefined;
  const [from, to] = temporalCoverage.replaceAll("..", "").split("/");
  return { from: formattedDate(from), to: formattedDate(to) };
}
