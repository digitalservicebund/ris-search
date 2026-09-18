import { formatArray, truncateAtWord } from "~/utils/textFormatting";

export type SecondaryTitleInput = {
  decisionNames: string[];
  titleLine?: string;
};

export function getCaselawSecondaryTitle(
  { decisionNames, titleLine }: SecondaryTitleInput,
  truncate = true,
): string | undefined {
  const nonBlankDecisionNames = decisionNames.filter((name) => name.trim());
  const formattedDecisionNames = formatArray(nonBlankDecisionNames);
  const title = formattedDecisionNames ?? titleLine;

  if (!title) return undefined;
  return truncate ? truncateAtWord(title, 90, true) : title;
}
