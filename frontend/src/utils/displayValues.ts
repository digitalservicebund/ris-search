import { BadgeColor } from "~/components/Badge.vue";
import { DocumentKind } from "~/types";
import {
  getValidityStatus,
  getValidityStatusLabel,
  temporalCoverageToValidityInterval,
} from "~/utils/norm";

const documentKindDisplayValues: Record<DocumentKind, string> = {
  [DocumentKind.CaseLaw]: "Gerichtsentscheidungen",
  [DocumentKind.Norm]: "Gesetze & Verordnungen",
  [DocumentKind.Literature]: "Literaturnachweise",
  [DocumentKind.AdministrativeDirective]: "Verwaltungsvorschriften",
  [DocumentKind.All]: "Datensätze",
};

/**
 * Returns a human readable label for the specified document kind.
 *
 * @param documentKind Document kind to be displayed
 * @returns Display value
 */
export function formatDocumentKind(documentKind: DocumentKind): string {
  return documentKindDisplayValues[documentKind] ?? documentKindDisplayValues.A;
}

/**
 * Returns a human readable label and status color for the validity status of
 * a norm.
 *
 * @param temporalCoverage Temporal coverage, must conform to the format of
 *  LegislationExpression.workExample
 * @returns Formatted status or undefined if no status can be determined based
 *  on the temporal coverage
 */
export function formatNormValidity(
  temporalCoverage: string,
): { label: string; color: BadgeColor } | undefined {
  let interval;
  try {
    interval = temporalCoverageToValidityInterval(temporalCoverage);
  } catch {
    return undefined;
  }

  const validityStatus = getValidityStatus(interval);
  const label = getValidityStatusLabel(validityStatus);

  let color: BadgeColor;

  switch (validityStatus) {
    case "InForce":
      color = BadgeColor.GREEN;
      break;
    case "FutureInForce":
      color = BadgeColor.YELLOW;
      break;
    case "Expired":
      color = BadgeColor.RED;
      break;
    default:
      color = BadgeColor.BLUE;
      break;
  }

  return label ? { label, color } : undefined;
}

/**
 * Formats the count of search results.
 *
 * @param count - Result count
 * @returns Formatted count
 */
export function formatResultCount(count: number) {
  if (count === 0) {
    return "Keine Suchergebnisse gefunden";
  } else if (count === 1) {
    return "1 Suchergebnis";
  } else if (count === 10_000) {
    return "Mehr als 10.000 Suchergebnisse";
  }

  return `${formatNumberWithSeparators(count)} Suchergebnisse`;
}
