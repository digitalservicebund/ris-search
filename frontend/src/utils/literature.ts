import type { MetadataItem } from "~/components/Metadata.vue";
import type { Literature } from "~/types";
import { formatArray, formatNames } from "~/utils/textFormatting";

export const LITERATURE_TITLE_PLACEHOLDER = "Titelzeile nicht vorhanden";

export function getTitle(literature?: Partial<Literature>) {
  return [
    literature?.headline,
    literature?.alternativeHeadline,
    literature?.headlineAdditions,
  ].find((title) => title !== null && title !== undefined);
}

export function getLiteratureMetadataItems(
  literature?: Partial<Literature>,
): MetadataItem[] {
  const references = [
    ...(literature?.dependentReferences ?? []),
    ...(literature?.independentReferences ?? []),
  ];
  return [
    {
      label: "Dokumenttyp",
      value: formatArray(literature?.documentTypes ?? []),
    },
    {
      label: "Fundstelle",
      value: formatArray(references),
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
): { label: string; value?: string; valueList?: string[] }[] {
  if (literature?.literatureType == "sli") {
    return getSliLiteratureDetailItems(literature);
  }
  return getUliLiteratureDetailItems(literature);
}

export function getUliLiteratureDetailItems(
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

export function getSliLiteratureDetailItems(
  literature?: Partial<Literature>,
): { label: string; value?: string; valueList?: string[] }[] {
  const normReferences = literature?.normReferences ?? [];
  const languages = literature?.languages ?? [];
  const conferenceNotes = literature?.conferenceNotes ?? [];
  const universityNotes = literature?.universityNotes ?? [];
  return [
    {
      label: getSingularOrPlural("Norm:", "Normen:", normReferences.length),
      value: formatArray(normReferences),
    },
    {
      label: "Bearbeiter:",
      value: formatArray(formatNames(literature?.editors ?? [])),
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
      label: "Begründer:",
      value: formatArray(formatNames(literature?.founder ?? [])),
    },
    {
      label: "Herausgeber:",
      value: formatArray(formatNames(literature?.publishers ?? [])),
    },
    {
      label: "Verlag:",
      value: formatArray(formatNames(literature?.publishingHouses ?? [])),
    },
    {
      label: "Ausgabe:",
      value: literature?.edition ?? undefined,
    },
    {
      label: "Bestellnummer:",
      value: formatArray(literature?.internationalIdentifiers ?? []),
    },
    {
      label: "Teilband:",
      valueList: literature?.volumes,
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
    {
      label: getSingularOrPlural(
        "Hochschule:",
        "Hochschulen:",
        universityNotes.length,
      ),
      value: formatArray(universityNotes),
    },
  ];
}
