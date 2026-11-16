import type {
  NachgehendeEntscheidung,
  VorgehendeEntscheidung,
} from "~/types/api";
import { dateFormattedLongGerman } from "~/utils/dateFormatting";
import { type DocumentLink, toDocumentLink } from "~/utils/documentLink";
import {
  formatArray,
  isNotBlank,
  truncateAtWord,
} from "~/utils/textFormatting";

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

export function formatVerknuepfteEntscheidung(
  entscheidung: VorgehendeEntscheidung | NachgehendeEntscheidung,
): string | undefined {
  const { gericht, dokumenttyp, entscheidungsdatum, aktenzeichen } =
    entscheidung;

  const gerichtFormatted = [gericht?.gerichtstyp, gericht?.gerichtsort]
    .filter(isNotBlank)
    .join(" ");
  const datumFormatted = dateFormattedLongGerman(entscheidungsdatum);

  let result = [gerichtFormatted, dokumenttyp].filter(isNotBlank).join(", ");

  if (datumFormatted) {
    result += `${result ? " " : ""}vom ${datumFormatted}`;
  }

  if (isNotBlank(aktenzeichen)) {
    result += `${result ? " - " : ""}${aktenzeichen}`;
  }

  if (!result) return undefined;

  const isAnhaengig =
    "art" in entscheidung && entscheidung.art?.toLowerCase() === "anhängig";
  return isAnhaengig ? `${result} (anhängig)` : result;
}

export function toVerknuepfteEntscheidungLink(
  entscheidung: VorgehendeEntscheidung | NachgehendeEntscheidung,
  from?: string,
): DocumentLink | undefined {
  const text = formatVerknuepfteEntscheidung(entscheidung);
  const { dokumentnummer } = entscheidung;

  return toDocumentLink(text, "/gerichtsentscheidungen", dokumentnummer, from);
}
