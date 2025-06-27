import { formattedDate, formattedDateToDateTime } from "~/utils/dateFormatting";
import type { LegislationExpression, LegislationWork } from "~/types";

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

export enum ExpressionStatus {
  InForce = "Aktuell gültig",
  Future = "Zukünftig in Kraft",
  Historical = "Außer Kraft",
}

export function getExpressionStatus(
  expression: LegislationExpression,
): ExpressionStatus | undefined {
  const { from: startDate, to: endDate } =
    temporalCoverageToValidityInterval(expression.temporalCoverage) || {};
  const legalForceStatus = expression.legislationLegalForce;
  if (legalForceStatus === "InForce") {
    return ExpressionStatus.InForce;
  } else {
    if (
      startDate &&
      formattedDateToDateTime(startDate) > getCurrentDateInGermany().toDate()
    ) {
      return ExpressionStatus.Future;
    }
    if (
      endDate &&
      formattedDateToDateTime(endDate) < getCurrentDateInGermany().toDate()
    ) {
      return ExpressionStatus.Historical;
    }
  }
  return undefined;
}

export function getManifestationUrl(
  metadata: LegislationWork | undefined,
  backendURL: string,
  format: string,
) {
  const encoding = metadata?.workExample?.encoding.find(
    (e) => e.encodingFormat === format,
  );
  return encoding?.contentUrl ? backendURL + encoding.contentUrl : undefined;
}
