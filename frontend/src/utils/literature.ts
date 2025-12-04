import type { MetadataItem } from "~/components/Metadata.vue";
import type { Literature } from "~/types";
import { formatArray, formatNames } from "~/utils/textFormatting";

export const LITERATURE_TITLE_PLACEHOLDER = "Titelzeile nicht vorhanden";

export function isLiteratureEmpty(literature?: Partial<Literature>) {
  const titles = getTitles(literature);
  const hasOneOrZeroTitles = !titles || titles.length < 2;

  return !literature?.outline && !literature?.shortReport && hasOneOrZeroTitles;
}

export function getTitle(literature?: Partial<Literature>) {
  return getTitles(literature)[0];
}

export function getLiteratureMetadataItems(
  literature?: Partial<Literature>,
): MetadataItem[] {
  return [
    {
      label: "Dokumenttyp",
      value: formatArray(literature?.documentTypes ?? []),
    },
    {
      label: "Fundstelle",
      value: formatArray(literature?.dependentReferences ?? []),
    },

    {
      label: "Autor",
      value: formatArray(formatNames(literature?.authors ?? [])),
    },
    {
      label: "Veröffentlichungsjahr",
      value: formatArray(literature?.yearsOfPublication ?? []),
    },
  ];
}

export function getLiteratureDetailItems(
  literature?: Partial<Literature>,
): { label: string; value?: string }[] {
  const normReferences = literature?.normReferences ?? [];
  const languages = literature?.languages ?? [];
  const conferenceNotes = literature?.conferenceNotes ?? [];
  return [
    {
      label: getSingularOrPlural("Norm:", "Normen:", normReferences.length),
      value: formatArray(normReferences),
    },
    {
      label: "Mitarbeiter:",
      value: formatArray(formatNames(literature?.collaborators ?? [])),
    },
    {
      label: "Urheber:",
      value: formatArray(formatNames(literature?.originators ?? [])),
    },
    {
      label: getSingularOrPlural("Sprache:", "Sprachen:", languages.length),
      value: formatArray(languages),
    },
    {
      label: getSingularOrPlural(
        "Kongress:",
        "Kongresse:",
        conferenceNotes.length,
      ),
      value: formatArray(conferenceNotes),
    },
  ];
}

function getTitles(literature?: Partial<Literature>): string[] {
  return [
    literature?.headline,
    literature?.alternativeHeadline,
    literature?.headlineAdditions,
  ].filter((title) => title !== null && title !== undefined);
}
