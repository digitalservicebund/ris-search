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
  caseLaw?: Pick<CaseLaw, "decisionName" | "titleLine">,
): string | undefined {
  const decisionNames = caseLaw?.decisionName.filter((name) => name.trim());
  const formattedDecisionNames = formatArray(decisionNames ?? []);
  const title = formattedDecisionNames ?? caseLaw?.titleLine;
  return title ? truncateAtWord(title, 90) : undefined;
}
