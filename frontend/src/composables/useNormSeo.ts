import type { LegislationExpression } from "~/types/api";
import type { Dayjs } from "dayjs";
import { getValidityStatusLabel, type ValidityStatus } from "~/utils/norm";

export type UseNormSeoInput = {
  norm: LegislationExpression;
  validityInterval?: ValidityInterval;
  validityStatus?: ValidityStatus;
};

export function useNormSeo({
  norm,
  validityInterval,
  validityStatus,
}: UseNormSeoInput) {
  useSeo({
    title: buildTitle(norm, validityInterval, validityStatus),
    description: buildDescription(norm),
    ogTitle: buildOgTitle(norm, validityInterval?.from, validityStatus),
  });
}

function buildTitle(
  norm: LegislationExpression,
  validityInterval?: ValidityInterval,
  validityStatus?: ValidityStatus,
) {
  const validityIntervalPart = buildValidityTitleLabel(validityInterval);
  const validityStatusPart = validityStatus
    ? `, ${getValidityStatusLabel(validityStatus)}`
    : "";

  return `${norm.abbreviation ?? "Gesetz"}${validityIntervalPart}${validityStatusPart}`;
}

/**
 * Build a label to be used as part of a page title. If both dates are available
 * it returns a range e.g. `, 01.01.2025-01.01.2026`. If only one of the dates exists,
 * the date prefixed with a `vom` or `bis` is returned.
 * NOTE: the string is prefixed with a `, ` so it can directly be inserted in to a title string.
 * @param validityInterval
 */
export function buildValidityTitleLabel(validityInterval?: ValidityInterval) {
  const validFrom = dateFormattedDDMMYYYY(validityInterval?.from);
  const validTo = dateFormattedDDMMYYYY(validityInterval?.to);
  if (validFrom && validTo) {
    return `, ${validFrom}-${validTo}`;
  } else if (validFrom) {
    return `, vom ${validFrom}`;
  } else if (validTo) {
    return `, bis ${validTo}`;
  }

  return "";
}

function buildDescription(norm: LegislationExpression) {
  const shortTitle = norm.alternateName?.trim();
  const longTitle = norm.name?.trim();
  const fullDescription = shortTitle ?? longTitle;
  return fullDescription ? truncateAtWord(fullDescription, 150) : "";
}

function buildOgTitle(
  norm: LegislationExpression,
  validFrom?: Dayjs,
  normValidityStatus?: ValidityStatus,
) {
  const shortTitle = norm.alternateName?.trim();
  const baseTitle = norm.abbreviation?.trim() || shortTitle;

  if (!baseTitle) return undefined;

  const parts: string[] = [];

  const formattedValidFrom = dateFormattedDDMMYYYY(validFrom);
  if (formattedValidFrom) parts.push(`Fassung vom ${formattedValidFrom}`);

  const statusLabel = getValidityStatusLabel(normValidityStatus);
  if (statusLabel) parts.push(statusLabel);

  let result = baseTitle;
  if (parts.length) result += `: ${parts.join(", ")}`;

  return truncateAtWord(result, 55) || undefined;
}
