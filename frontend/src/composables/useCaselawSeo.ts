import { type Rechtsprechung } from "~/types/api";

export type UseCaselawSeoInput = {
  rechtsprechung?: Rechtsprechung;
  document?: Document;
};

export function useCaselawSeo({
  rechtsprechung,
  document,
}: UseCaselawSeoInput) {
  useSeo({
    title: buildTitle(rechtsprechung),
    description: buildDescription(rechtsprechung, document),
    ogTitle: buildOgTitle(rechtsprechung),
  });
}

function buildTitle(rechtsprechung?: Rechtsprechung) {
  return rechtsprechung?.kurztitel || "Gerichtsentscheidung";
}

function buildDescription(
  rechtsprechung: Rechtsprechung | undefined,
  document: Document | undefined,
) {
  if (rechtsprechung?.leitsatz) {
    const sentences = rechtsprechung.leitsatz
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
function buildOgTitle(rechtsprechung?: Rechtsprechung) {
  const fallback = "Gerichtsentscheidung";
  if (!rechtsprechung) return fallback;

  const court = rechtsprechung.gericht?.trim() || "";
  const dtype = rechtsprechung.dokumenttyp || fallback;
  const date = rechtsprechung.datum
    ? dateFormattedDDMMYYYY(rechtsprechung.datum)
    : "";
  const file = rechtsprechung.aktenzeichenListe?.[0] || "";

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
