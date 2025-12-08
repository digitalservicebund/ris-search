import dayjs, { type Dayjs } from "dayjs";
import _ from "lodash";
import type { LegislationWork, SearchResult } from "~/types";
import {
  getCurrentDateInGermany,
  getCurrentDateInGermanyFormatted,
  parseDateGermanLocalTime,
} from "~/utils/dateFormatting";

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

export type ValidityStatus = "InForce" | "FutureInForce" | "Expired";

export function getValidityStatusLabel(
  status?: ValidityStatus,
): string | undefined {
  switch (status) {
    case "Expired":
      return "Außer Kraft";
    case "InForce":
      return "Aktuell gültig";
    case "FutureInForce":
      return "Zukünftig in Kraft";
    default:
      return undefined;
  }
}

export function getValidityStatus(
  validityInterval?: ValidityInterval,
): ValidityStatus | undefined {
  if (!validityInterval?.from && !validityInterval?.to) return undefined;

  const currentDate = getCurrentDateInGermany();
  const start = validityInterval?.from ?? dayjs(new Date("0000-01-01"));
  const end = validityInterval?.to ?? dayjs(new Date("9999-12-31"));

  if (end.isBefore(currentDate, "day")) {
    return "Expired";
  }

  if (
    (start.isBefore(currentDate, "day") || start.isSame(currentDate, "day")) &&
    (end.isSame(currentDate, "day") || end.isAfter(currentDate, "day"))
  ) {
    return "InForce";
  }

  if (start.isAfter(currentDate, "day")) {
    return "FutureInForce";
  }

  return undefined;
}

export function getManifestationUrl(
  metadata: LegislationWork | undefined,
  format: string,
) {
  const encoding = metadata?.workExample?.encoding.find(
    (e) => e.encodingFormat === format,
  );
  return encoding?.contentUrl;
}

export function getNormBreadcrumbTitle(norm: LegislationWork): string {
  return norm.abbreviation || norm.alternateName || norm.name || "";
}

export function getNormTitle(norm: LegislationWork): string {
  return norm.name || norm.alternateName || norm.abbreviation || "";
}

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
