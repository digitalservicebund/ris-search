import { normalizeSpaces } from "~/utils/textFormatting";
import type { Article } from "~/types/api";
import { buildValidityTitleLabel } from "~/composables/useNormSeo";
import { temporalCoverageToValidityInterval } from "~/utils/norm";

export type UseArticleSeoInput = {
  abbreviation?: string;
  article?: Article;
  articleHeadlineHtml?: string;
  articleHtml?: string;
};

export function useArticleSeo({
  abbreviation,
  article,
  articleHeadlineHtml,
  articleHtml,
}: UseArticleSeoInput) {
  useSeo({
    title: buildTitle(abbreviation, article),
    description: buildDescription(articleHtml),
    ogTitle: buildOgTitle(abbreviation, articleHeadlineHtml),
  });
}

function buildTitle(abbreviation?: string, article?: Article) {
  const paragraphNumber = article?.name ? `, ${article.name}` : "";
  const validityIntervalLabel = buildValidityTitleLabel(
    temporalCoverageToValidityInterval(article?.temporalCoverage),
  );
  return `${abbreviation ?? "Gesetz"}${paragraphNumber}${validityIntervalLabel}`;
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
