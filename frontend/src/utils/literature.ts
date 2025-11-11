import type { Literature } from "~/types";

export function isDocumentEmpty(literature?: Literature) {
  const titles = getTitles(literature);
  const hasOneOrZeroTitles = !titles || titles.length < 2;

  return !literature?.outline && !literature?.shortReport && hasOneOrZeroTitles;
}

export function getTitle(literature?: Literature) {
  return getTitles(literature)[0];
}

function getTitles(literature?: Literature): string[] {
  return [
    literature?.headline,
    literature?.alternativeHeadline,
    literature?.headlineAdditions,
  ].filter((title) => title !== null && title !== undefined);
}
