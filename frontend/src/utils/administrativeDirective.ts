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
