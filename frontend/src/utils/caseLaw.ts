import type { DetailsListItem } from "~/components/documents/DetailsListV2.vue";
import { type CaseLaw } from "~/types/api";
import { formatArray, truncateAtWord } from "~/utils/textFormatting";

export function getEncodingURL(
  caseLaw: CaseLaw | null | undefined,
  format: string,
) {
  const encoding = caseLaw?.encoding?.find((e) => e.encodingFormat === format);
  return encoding?.contentUrl;
}

export function getCaseLawDetailItems(
  caseLaw?: Partial<CaseLaw>,
): DetailsListItem[] {
  const decisionNames = formatArray(caseLaw?.decisionName ?? []);
  const zipUrl = caseLaw?.encoding?.find(
    (e) => e.encodingFormat === "application/zip",
  )?.contentUrl;

  return [
    {
      type: "text",
      label: "Spruchkörper:",
      value: caseLaw?.judicialBody,
    },
    {
      type: "text",
      label: "ECLI:",
      value: caseLaw?.ecli,
      valueClass: "break-all",
    },
    {
      type: "text",
      label: "Entscheidungsname:",
      value: decisionNames,
    },
    {
      type: "link",
      label: "Download:",
      url: zipUrl,
      text: "Diese Gerichtsentscheidung als ZIP herunterladen",
      dataAttr: "xml-zip-view",
    },
  ];
}

export function getCaselawSecondaryTitle(
  caseLaw: Pick<CaseLaw, "decisionName" | "titleLine"> | undefined,
  truncate = true,
): string | undefined {
  const decisionNames = caseLaw?.decisionName.filter((name) => name.trim());
  const formattedDecisionNames = formatArray(decisionNames ?? []);
  const title = formattedDecisionNames ?? caseLaw?.titleLine;

  if (!title) return undefined;
  return truncate ? truncateAtWord(title, 90, true) : title;
}
