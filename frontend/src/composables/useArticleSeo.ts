import { buildValidityTitleLabel } from "~/composables/useNormSeo";
import { temporalCoverageToValidityInterval } from "~/utils/norm";
import { normalizeSpaces } from "~/utils/textFormatting";

export type UseArticleSeoInput = {
  abbreviation?: string;
  name?: string;
  temporalCoverage?: string;
  /** Text of the article heading */
  headline?: string;
  /** Text of the first paragraph of the article */
  firstParagraph?: string;
};

export function useArticleSeo({
  abbreviation,
  name,
  temporalCoverage,
  headline,
  firstParagraph,
}: UseArticleSeoInput) {
  const privateFeaturesEnabled = usePrivateFeaturesFlag();
  useSeo({
    title: buildTitle(
      abbreviation,
      name,
      privateFeaturesEnabled ? temporalCoverage : undefined,
    ),
    description: buildDescription(firstParagraph),
    ogTitle: buildOgTitle(abbreviation, headline),
  });
}

function buildTitle(
  abbreviation?: string,
  name?: string,
  temporalCoverage?: string,
) {
  const paragraphNumber = name ? `, ${name}` : "";
  const validityIntervalLabel = buildValidityTitleLabel(
    temporalCoverageToValidityInterval(temporalCoverage),
  );
  return `${abbreviation || "Gesetz"}${paragraphNumber}${validityIntervalLabel}`;
}

function buildDescription(firstParagraph?: string) {
  return firstParagraph ? truncateAtWord(firstParagraph, 150) : undefined;
}

function buildOgTitle(abbreviation?: string, headline?: string) {
  const normalizedHeadline = headline ? normalizeSpaces(headline) : undefined;

  const base = abbreviation || normalizedHeadline;
  if (!base) return undefined;

  const full =
    abbreviation && normalizedHeadline
      ? `${abbreviation}: ${normalizedHeadline}`
      : base;

  return truncateAtWord(full, 55) || undefined;
}
