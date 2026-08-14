import type { DetailsListItem } from "~/components/documents/DetailsList.vue";
import type { MetadataItem } from "~/components/documents/Metadata.vue";
import type { AdministrativeDirective } from "~/types/api";

export function getAdministrativeDirectiveMetadataItems(
  administrativeDirective?: Partial<AdministrativeDirective>,
): MetadataItem[] {
  return [
    {
      type: "badge",
      label: "Aktenzeichen",
      values: administrativeDirective?.referenceNumbers ?? [],
    },
    {
      type: "text",
      label: "Normgeber",
      value: administrativeDirective?.legislationAuthority,
    },
    {
      type: "text",
      label: "Dokumenttyp",
      value: administrativeDirective?.documentType,
    },
    {
      type: "text",
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
      type: "badge",
      label: getSingularOrPlural(
        "Fundstelle:",
        "Fundstellen:",
        references.length,
      ),
      values: references,
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
      type: "badge",
      label: getSingularOrPlural("Norm:", "Normen:", norms.length),
      values: norms,
    },
  ];
}
