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
  return text !== undefined ? sanitizeSearchResult(text) : undefined;
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
