import { buildValidityTitleLabel } from "~/composables/useNormSeo";
import { temporalCoverageToValidityInterval } from "~/utils/norm";
import { normalizeSpaces } from "~/utils/textFormatting";

export type UseArticleSeoInput = {
  abbreviation?: string;
  name?: string;
  temporalCoverage?: string;
  articleHeadlineHtml?: string;
  articleHtml?: string;
};

export function useArticleSeo({
  abbreviation,
  name,
  temporalCoverage,
  articleHeadlineHtml,
  articleHtml,
}: UseArticleSeoInput) {
  const privateFeaturesEnabled = usePrivateFeaturesFlag();
  useSeo({
    title: buildTitle(
      abbreviation,
      name,
      privateFeaturesEnabled ? temporalCoverage : undefined,
    ),
    description: buildDescription(articleHtml),
    ogTitle: buildOgTitle(abbreviation, articleHeadlineHtml),
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

function buildDescription(articleHtml?: string) {
  if (!articleHtml) return undefined;
  const doc = parseDocument(articleHtml);
  const firstParagraph = doc.querySelector("p");
  const text = firstParagraph?.textContent?.trim();
  return text ? truncateAtWord(text, 150) : undefined;
}

function buildOgTitle(abbreviation?: string, articleHeadlineHtml?: string) {
  const headline = articleHeadlineHtml
    ? normalizeSpaces(
        parseDocument(articleHeadlineHtml).body?.textContent ?? "",
      )
    : undefined;

  const base = abbreviation || headline;
  if (!base) return undefined;

  const full = abbreviation && headline ? `${abbreviation}: ${headline}` : base;

  return truncateAtWord(full, 55) || undefined;
}
