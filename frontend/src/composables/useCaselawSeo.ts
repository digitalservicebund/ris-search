import { type Rechtsprechung } from "~/types/api";

export type UseCaselawSeoInput = {
  caseLaw?: Rechtsprechung;
  document?: Document;
};

export function useCaselawSeo({ caseLaw, document }: UseCaselawSeoInput) {
  useSeo({
    title: buildTitle(caseLaw),
    description: buildDescription(caseLaw, document),
    ogTitle: buildOgTitle(caseLaw),
  });
}

function buildTitle(caseLaw?: Rechtsprechung) {
  return caseLaw?.kurztitel || "Gerichtsentscheidung";
}

function buildDescription(
  caseLaw: Rechtsprechung | undefined,
  document: Document | undefined,
) {
  if (caseLaw?.leitsatz) {
    const sentences = caseLaw.leitsatz.split(/(?<=[.!?])\s+/).filter(Boolean);

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
function buildOgTitle(caseLaw?: Rechtsprechung) {
  const fallback = "Gerichtsentscheidung";
  if (!caseLaw) return fallback;

  const court = caseLaw.gericht?.trim() || "";
  const dtype = caseLaw.dokumenttyp || fallback;
  const date = caseLaw.datum ? dateFormattedDDMMYYYY(caseLaw.datum) : "";
  const file = caseLaw.aktenzeichenListe?.[0] || "";

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
