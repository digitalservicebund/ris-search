import type { ArtikelFassung } from "~/types/api";
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

const LEGISLATION_ID_PREFIX = "/v1/legislation/";

/**
 * Returns the ELIs of all expressions an article version is part of, without
 * the API prefix, e.g. `eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu`.
 */
export function getExpressionElis(articleVersion: ArtikelFassung): string[] {
  const ids = (articleVersion.isPartOf ?? []).map((ref) => ref["@id"]);
  return ids.map((id) => id.replace(LEGISLATION_ID_PREFIX, ""));
}

// eli/{jurisdiction}/{agent}/{year}/{naturalIdentifier}/{pointInTime}/{version}/{language}
function parseExpressionEli(eli: string) {
  const segments = eli.split("/");
  const pointInTime = segments[5] ?? "";
  const version = Number(segments[6]) || 0;
  return { pointInTime, version };
}

function compareNewestFirst(eliA: string, eliB: string) {
  const a = parseExpressionEli(eliA);
  const b = parseExpressionEli(eliB);

  if (a.pointInTime !== b.pointInTime) {
    return b.pointInTime.localeCompare(a.pointInTime);
  }

  return b.version - a.version;
}

/**
 * Picks the newest expression ELI based on the point in time and the version
 * encoded in the ELIs.
 */
export function getNewestExpressionEli(
  expressionElis: string[],
): string | undefined {
  const [newest] = expressionElis.toSorted(compareNewestFirst);
  return newest;
}
