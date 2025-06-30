import { parseDateGermanLocalTime } from "~/utils/dateFormatting";
import type { LegislationExpression, LegislationWork } from "~/types";
import type { Dayjs } from "dayjs";

export interface ValidityInterval {
  from?: Dayjs;
  to?: Dayjs;
}

export function temporalCoverageToValidityInterval(
  temporalCoverage: string | null | undefined,
): ValidityInterval | undefined {
  if (!temporalCoverage) return undefined;
  const [from, to] = temporalCoverage.replaceAll("..", "").split("/");
  return {
    from: parseDateGermanLocalTime(from),
    to: parseDateGermanLocalTime(to),
  };
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
  const currentDate = getCurrentDateInGermany();

  if (
    legalForceStatus === "InForce" ||
    (startDate &&
      startDate <= currentDate &&
      (!endDate || endDate >= currentDate))
  ) {
    return ExpressionStatus.InForce;
  }

  if (startDate && startDate.isAfter(currentDate, "day")) {
    return ExpressionStatus.Future;
  }

  if (endDate && endDate.isBefore(currentDate, "day")) {
    return ExpressionStatus.Historical;
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
