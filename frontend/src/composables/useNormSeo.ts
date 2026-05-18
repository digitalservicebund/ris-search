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
  const validFrom = dateFormattedDDMMYYYY(validityInterval?.from);
  const validTo = dateFormattedDDMMYYYY(validityInterval?.to);
  let validityIntervalPart = "";
  if (validFrom && validTo) {
    validityIntervalPart = `, ${validFrom}-${validTo}`;
  } else if (validFrom) {
    validityIntervalPart = `, vom ${validFrom}`;
  } else if (validTo) {
    validityIntervalPart = `, bis ${validTo}`;
  }

  const validityStatusPart = validityStatus
    ? `, ${getValidityStatusLabel(validityStatus)}`
    : "";

  return `${norm.abbreviation ?? ""}${validityIntervalPart}${validityStatusPart}`;
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
