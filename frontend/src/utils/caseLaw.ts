import { type CaseLaw } from "~/types/api";
import { formatArray, truncateAtWord } from "~/utils/textFormatting";

export function getEncodingURL(
  caseLaw: CaseLaw | null | undefined,
  format: string,
) {
  const encoding = caseLaw?.encoding?.find((e) => e.encodingFormat === format);
  return encoding?.contentUrl;
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
