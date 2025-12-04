import type { MetadataItem } from "~/components/Metadata.vue";
import type { AdministrativeDirective } from "~/types";

export const ADMINISTRATIVE_DIRECTIVE_TITLE_PLACEHOLDER =
  "Titelzeile nicht vorhanden";

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

export function isAdministrativeDirectiveEmpty(
  administrativeDirective?: Partial<AdministrativeDirective>,
) {
  return (
    !administrativeDirective?.shortReport?.length &&
    !administrativeDirective?.outline?.length
  );
}

export function getAdministrativeDirectiveDetailItems(
  administrativeDirective?: Partial<AdministrativeDirective>,
): { label: string; value?: string }[] {
  const formattedCitationDates =
    administrativeDirective?.citationDates
      ?.map(dateFormattedDDMMYYYY)
      .filter((date) => date !== undefined) ?? [];

  const references = administrativeDirective?.references ?? [];
  const norms = administrativeDirective?.normReferences ?? [];

  return [
    {
      label: getSingularOrPlural(
        "Fundstelle:",
        "Fundstellen:",
        references.length,
      ),
      value: formatArray(references),
    },
    {
      label: getSingularOrPlural(
        "Zitierdatum:",
        "Zitierdaten:",
        formattedCitationDates.length,
      ),
      value: formatArray(formattedCitationDates),
    },
    {
      label: "Gültig bis:",
      value: dateFormattedDDMMYYYY(administrativeDirective?.expiryDate),
    },
    {
      label: "Dokumenttyp Zusatz:",
      value: administrativeDirective?.documentTypeDetail,
    },
    {
      label: getSingularOrPlural("Norm:", "Normen:", norms.length),
      value: formatArray(norms),
    },
  ];
}
