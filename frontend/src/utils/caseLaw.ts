import type {
  NachgehendeEntscheidung,
  VorgehendeEntscheidung,
} from "~/types/api";
import { dateFormattedLongGerman } from "~/utils/dateFormatting";
import { formatArray, truncateAtWord } from "~/utils/textFormatting";

export type SecondaryTitleInput = {
  decisionNames: string[];
  titleLine?: string;
};

export function getCaselawSecondaryTitle(
  { decisionNames, titleLine }: SecondaryTitleInput,
  truncate = true,
): string | undefined {
  const nonBlankDecisionNames = decisionNames.filter((name) => name.trim());
  const formattedDecisionNames = formatArray(nonBlankDecisionNames);
  const title = formattedDecisionNames ?? titleLine;

  if (!title) return undefined;
  return truncate ? truncateAtWord(title, 90, true) : title;
}

function isPresent<T>(value: T | null | undefined): value is T {
  return value !== null && value !== undefined;
}

/**
 * Formats a linked decision as "Gerichtstyp Gerichtsort, Dokumenttyp vom Datum
 *
 * - Aktenzeichen", appending " (anhängig)" for pending proceedings.
 */
export function formatVerknuepfteEntscheidung(
  entscheidung: VorgehendeEntscheidung | NachgehendeEntscheidung,
): string | undefined {
  const { gericht, dokumenttyp, entscheidungsdatum, aktenzeichen } =
    entscheidung;

  const gerichtFormatted = [gericht?.gerichtstyp, gericht?.gerichtsort]
    .filter(isPresent)
    .join(" ");
  const datumFormatted = dateFormattedLongGerman(entscheidungsdatum);

  let result = [gerichtFormatted.trim() ? gerichtFormatted : null, dokumenttyp]
    .filter(isPresent)
    .join(", ");

  if (isPresent(datumFormatted)) {
    result += `${result ? " " : ""}vom ${datumFormatted}`;
  }

  if (isPresent(aktenzeichen)) {
    result += `${result ? " - " : ""}${aktenzeichen}`;
  }

  if (!result) return undefined;

  const isAnhaengig =
    "art" in entscheidung && entscheidung.art?.toLowerCase() === "anhängig";
  return isAnhaengig ? `${result} (anhängig)` : result;
}
