import dayjs, { type Dayjs } from "dayjs";
import { partition, sortBy } from "lodash-es";
import type { MetadataItem } from "~/components/documents/Metadata.vue";
import type { LegislationExpression } from "~/types/api";
import {
  dateFormattedDDMMYYYY,
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
  metadata: LegislationExpression | undefined,
  format: string,
) {
  const encoding = metadata?.encoding?.find((e) => e.encodingFormat === format);
  return encoding?.contentUrl;
}

/**
 * Extracts the bare eId from an einzelnorm cross-link found inside transformed
 * legislation HTML. The backend generates these links relative to the API URL
 * structure, shaped like `{subtype}/{eId}.html` (e.g.
 * `regelungstext-1/art-z1.html`). The portal route only carries the bare eId
 * (the manifestation/subtype are re-derived from metadata), so we strip the
 * subtype prefix and the `.html` suffix.
 *
 * @returns The bare eId, or null if the href is not an einzelnorm link (e.g. a
 *   hash-only, absolute, or resource link).
 */
export function getEinzelnormEIdFromHref(href: string): string | null {
  const match = /^[^/#?:]+\/([^/#?]+)\.html$/.exec(href);
  return match?.[1] ?? null;
}

export function getNormBreadcrumbTitle(norm: LegislationExpression): string {
  return norm.abbreviation || norm.alternateName || norm.name || "";
}

export function getNormTitle(norm: LegislationExpression): string {
  return norm.name || norm.alternateName || norm.abbreviation || "";
}

export function getMostRelevantExpression(
  expressions: LegislationExpression[],
): string | null | undefined {
  if (expressions.length === 0) {
    return null;
  }
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
  const [future, past] = partition(
    expressions,
    (item) => item.temporalCoverage >= referenceDate,
  );
  if (future.length > 0) {
    return sortBy(future, "legislationLegalForce")[0]?.legislationIdentifier;
  }
  if (past.length > 0) {
    return (
      sortBy(past, "legislationLegalForce").at(-1)?.legislationIdentifier ??
      null
    );
  }
  throw new Error("Could not identify the most relevant expression");
}

export function getNormMetadataItems(
  norm?: Partial<LegislationExpression>,
): MetadataItem[] {
  const validityInterval = temporalCoverageToValidityInterval(
    norm?.temporalCoverage,
  );
  const formattedStatus = getValidityStatusLabel(
    getValidityStatus(validityInterval),
  );
  return [
    {
      label: "Abkürzung",
      value: norm?.abbreviation,
    },
    {
      label: "Status",
      value: formattedStatus,
    },
    {
      label: "Gültig ab",
      value: dateFormattedDDMMYYYY(validityInterval?.from),
    },
    {
      label: "Gültig bis",
      value: dateFormattedDDMMYYYY(validityInterval?.to),
    },
  ];
}

/**
 * Checks if the document has a `<div>` with class `akn-body` and checks if it's
 * empty.
 *
 * @param document
 */
export function isNormBodyEmpty(document?: Document): boolean {
  if (!document) {
    return true;
  }

  const aknBody = document.querySelector("div.akn-body");
  return !aknBody || aknBody.innerHTML.trim() === "";
}
