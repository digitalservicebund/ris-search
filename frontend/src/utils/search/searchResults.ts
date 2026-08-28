import type { TextMatch } from "~/types/api";
import { sanitizeSearchResult } from "~/utils/sanitize";

export const TITLE_FALLBACK = "Titelzeile nicht vorhanden";

/**
 * Returns the sanitized text of the first TextMatch with the given name, or
 * undefined if no match is found.
 */
export function getMatch(
  name: string,
  textMatches: TextMatch[],
): string | undefined {
  const text = textMatches.find((m) => m.name === name)?.text;
  return typeof text === "string" ? sanitizeSearchResult(text) : undefined;
}

/** Returns the sanitized texts of all TextMatches with the given name. */
export function getMatches(name: string, textMatches: TextMatch[]): string[] {
  return textMatches
    .filter((m) => m.name === name)
    .map((m) => sanitizeSearchResult(m.text));
}

/**
 * Returns the first truthy candidate, falling back to TITLE_FALLBACK. Sanitizes
 * the chosen value.
 */
export function getTitleWithFallback(
  ...candidates: (string | undefined | null)[]
): string {
  const value = candidates.find((c) => !!c) ?? TITLE_FALLBACK;
  return sanitizeSearchResult(value);
}

/** Heading levels a search result title can be rendered at. */
export type SearchResultHeadingLevel = "2" | "3";

/**
 * Returns the tag name and typography class for a search result title.
 *
 * @param level Heading level to render the title at
 * @returns Tag name and typography class to apply
 */
export function getSearchResultHeadline(level: SearchResultHeadingLevel) {
  return {
    tag: `h${level}`,
    class:
      level === "2"
        ? "typo-headline-searchresult"
        : "typo-headline-searchresult-compact",
  };
}
