import { type CaseLaw } from "~/types/api";

export function getEncodingURL(
  caseLaw: CaseLaw | null | undefined,
  format: string,
) {
  const encoding = caseLaw?.encoding?.find((e) => e.encodingFormat === format);
  return encoding?.contentUrl;
}

export function getCaselawSecondaryTitle(
  caseLaw?: Pick<CaseLaw, "decisionName" | "titleLine">,
): string | undefined {
  const decisionName = caseLaw?.decisionName.find((name) => name.trim());
  const title = decisionName ?? caseLaw?.titleLine;
  return title ? truncateAtWord(title, 90) : undefined;
}
