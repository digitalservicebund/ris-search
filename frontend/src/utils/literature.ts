import type { MetadataItem } from "~/components/Metadata.vue";
import type { Literature } from "~/types";

export const LITERATURE_TITLE_PLACEHOLDER = "Titelzeile nicht vorhanden";

export function isDocumentEmpty(literature?: Partial<Literature>) {
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

function getTitles(literature?: Partial<Literature>): string[] {
  return [
    literature?.headline,
    literature?.alternativeHeadline,
    literature?.headlineAdditions,
  ].filter((title) => title !== null && title !== undefined);
}
