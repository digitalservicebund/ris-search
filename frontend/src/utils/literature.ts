import type { Literature } from "~/types";

export function isDocumentEmpty(literature?: Literature) {
  return (
    !literature?.outline && !literature?.shortReport && !getTitle(literature)
  );
}

export function getTitle(literature?: Literature) {
  return (
    literature?.headline ??
    literature?.alternativeHeadline ??
    literature?.headlineAdditions ??
    undefined
  );
}
