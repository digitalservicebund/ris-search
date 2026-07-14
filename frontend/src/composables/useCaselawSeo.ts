import { type CaseLaw } from "~/types/api";

export type UseCaselawSeoInput = {
  caseLaw?: CaseLaw;
  document?: Document;
};

export function useCaselawSeo({ caseLaw, document }: UseCaselawSeoInput) {
  useSeo({
    title: buildTitle(caseLaw),
    description: buildDescription(caseLaw, document),
    ogTitle: buildOgTitle(caseLaw),
  });
}

function buildTitle(caseLaw?: CaseLaw) {
  return caseLaw?.headline || "Gerichtsentscheidung";
}

function buildDescription(
  caseLaw: CaseLaw | undefined,
  document: Document | undefined,
) {
  if (caseLaw?.guidingPrinciple) {
    const sentences = caseLaw.guidingPrinciple
      .split(/(?<=[.!?])\s+/)
      .filter(Boolean);

    return truncateAtWord(sentences.slice(0, 2).join(" "), 150);
  }

  if (document) {
    const firstParagraph = document.querySelector("section p");
    const firstParagraphText = firstParagraph?.textContent?.trim();
    if (firstParagraphText) {
      return truncateAtWord(firstParagraphText, 150);
    }
  }

  return "Gerichtsentscheidung";
}

// TODO: The truncation can cause parts of the fileNumber to not be displayed
// which is probably not the wanted behavior - this should be clarified and fixed in
// https://digitalservicebund.atlassian.net/browse/RISDEV-11649
function buildOgTitle(caseLaw?: CaseLaw) {
  const fallback = "Gerichtsentscheidung";
  if (!caseLaw) return fallback;

  const court = caseLaw.courtName?.trim() || "";
  const dtype = caseLaw.documentType || fallback;
  const date = caseLaw.decisionDate
    ? dateFormattedDDMMYYYY(caseLaw.decisionDate)
    : "";
  const file = caseLaw.fileNumbers?.[0] || "";

  const parts = [
    court && `${court}:`,
    dtype,
    date && `vom ${date}`,
    file && `– ${file}`,
  ]
    .filter(Boolean)
    .join(" ");

  return truncateAtWord(parts, 55) || undefined;
}
