import { formattedDate, formattedDateToDateTime } from "~/utils/dateFormatting";
import type { LegislationExpression } from "~/types";

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

export enum ExpresssionStatus {
  InForce = "Aktuell gültig",
  Future = "Zukünftig in Kraft",
  Historcial = "Außer Kraft",
}

export function getExpressionStatus(
  expression: LegislationExpression,
): ExpresssionStatus | undefined {
  const { from: startDate, to: endDate } =
    temporalCoverageToValidityInterval(expression.temporalCoverage) || {};
  const legalForceStatus = expression.legislationLegalForce;
  if (legalForceStatus === "InForce") {
    return ExpresssionStatus.InForce;
  } else {
    if (
      startDate &&
      formattedDateToDateTime(startDate) > getCurrentDateInGermany().toDate()
    ) {
      return ExpresssionStatus.Future;
    }
    if (
      endDate &&
      formattedDateToDateTime(endDate) < getCurrentDateInGermany().toDate()
    ) {
      return ExpresssionStatus.Historcial;
    }
  }
  return undefined;
}
