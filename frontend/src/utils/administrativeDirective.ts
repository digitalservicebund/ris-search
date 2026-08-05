import type { DetailsListItem } from "~/components/documents/DetailsList.vue";
import type { MetadataItem } from "~/components/documents/Metadata.vue";
import type { AdministrativeDirective } from "~/types/api";

export function getAdministrativeDirectiveMetadataItems(
  administrativeDirective?: Partial<AdministrativeDirective>,
): MetadataItem[] {
  return [
    {
      label: "Aktenzeichen",
      value: formatArray(administrativeDirective?.referenceNumbers ?? []),
    },
    {
      label: "Normgeber",
      value: administrativeDirective?.legislationAuthority,
    },

    {
      label: "Dokumenttyp",
      value: administrativeDirective?.documentType,
    },
    {
      label: "Gültig ab",
      value: dateFormattedDDMMYYYY(administrativeDirective?.entryIntoForceDate),
    },
  ];
}

export function getAdministrativeDirectiveDetailItems(
  administrativeDirective?: Partial<AdministrativeDirective>,
): DetailsListItem[] {
  const formattedCitationDates =
    administrativeDirective?.citationDates
      ?.map(dateFormattedDDMMYYYY)
      .filter((date) => date !== undefined) ?? [];

  const references = administrativeDirective?.references ?? [];
  const norms = administrativeDirective?.normReferences ?? [];

  return [
    {
      type: "text",
      label: getSingularOrPlural(
        "Fundstelle:",
        "Fundstellen:",
        references.length,
      ),
      value: formatArray(references),
    },
    {
      type: "text",
      label: getSingularOrPlural(
        "Zitierdatum:",
        "Zitierdaten:",
        formattedCitationDates.length,
      ),
      value: formatArray(formattedCitationDates),
    },
    {
      type: "text",
      label: "Gültig bis:",
      value: dateFormattedDDMMYYYY(administrativeDirective?.expiryDate),
    },
    {
      type: "text",
      label: "Dokumenttyp Zusatz:",
      value: administrativeDirective?.documentTypeDetail,
    },
    {
      type: "text",
      label: getSingularOrPlural("Norm:", "Normen:", norms.length),
      value: formatArray(norms),
    },
  ];
}
