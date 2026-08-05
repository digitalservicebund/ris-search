import type { DetailsListItem } from "~/components/documents/DetailsList.vue";
import type { MetadataItem } from "~/components/documents/Metadata.vue";
import type { Literature } from "~/types/api";

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
): DetailsListItem[] {
  if (literature?.literatureType == "sli") {
    return getSliLiteratureDetailItems(literature);
  }
  return getUliLiteratureDetailItems(literature);
}

export function getUliLiteratureDetailItems(
  literature?: Partial<Literature>,
): DetailsListItem[] {
  const normReferences = literature?.normReferences ?? [];
  const languages = literature?.languages ?? [];
  const conferenceNotes = literature?.conferenceNotes ?? [];
  return [
    {
      type: "text",
      label: getSingularOrPlural("Norm:", "Normen:", normReferences.length),
      value: formatArray(normReferences),
    },
    {
      type: "text",
      label: "Mitarbeiter:",
      value: formatArray(formatNames(literature?.collaborators ?? [])),
    },
    {
      type: "text",
      label: "Urheber:",
      value: formatArray(formatNames(literature?.originators ?? [])),
    },
    {
      type: "text",
      label: getSingularOrPlural("Sprache:", "Sprachen:", languages.length),
      value: formatArray(languages),
    },
    {
      type: "text",
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
): DetailsListItem[] {
  const normReferences = literature?.normReferences ?? [];
  const languages = literature?.languages ?? [];
  const conferenceNotes = literature?.conferenceNotes ?? [];
  const universityNotes = literature?.universityNotes ?? [];

  const publisherOrganizations = literature?.publisherOrganizations ?? [];
  const formattedPublishers = formatNames(literature?.publishers ?? []);
  const mergedPublishers = [...publisherOrganizations, ...formattedPublishers];

  return [
    {
      type: "text",
      label: getSingularOrPlural("Norm:", "Normen:", normReferences.length),
      value: formatArray(normReferences),
    },
    {
      type: "text",
      label: "Bearbeiter:",
      value: formatArray(formatNames(literature?.editors ?? [])),
    },
    {
      type: "text",
      label: "Mitarbeiter:",
      value: formatArray(formatNames(literature?.collaborators ?? [])),
    },
    {
      type: "text",
      label: "Urheber:",
      value: formatArray(formatNames(literature?.originators ?? [])),
    },
    {
      type: "text",
      label: "Begründer:",
      value: formatArray(formatNames(literature?.founder ?? [])),
    },
    {
      type: "text",
      label: "Herausgeber:",
      value: formatArray(mergedPublishers),
    },
    {
      type: "text",
      label: "Verlag:",
      value: formatArray(literature?.publishingHouses ?? []),
    },
    {
      type: "text",
      label: "Ausgabe:",
      value: literature?.edition ?? undefined,
    },
    {
      type: "text",
      label: "Bestellnummer:",
      value: formatArray(literature?.internationalIdentifiers ?? []),
    },
    {
      type: "list",
      label: "Teilband:",
      values: literature?.volumes ?? [],
    },
    {
      type: "text",
      label: getSingularOrPlural("Sprache:", "Sprachen:", languages.length),
      value: formatArray(languages),
    },
    {
      type: "text",
      label: getSingularOrPlural(
        "Kongress:",
        "Kongresse:",
        conferenceNotes.length,
      ),
      value: formatArray(conferenceNotes),
    },
    {
      type: "text",
      label: getSingularOrPlural(
        "Hochschule:",
        "Hochschulen:",
        universityNotes.length,
      ),
      value: formatArray(universityNotes),
    },
  ];
}
