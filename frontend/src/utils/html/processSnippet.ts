import sanitizeHtml from "sanitize-html";

export const SEARCH_SNIPPET_ALLOWLIST: sanitizeHtml.IOptions = {
  allowedTags: ["b", "i", "mark"],
  allowedAttributes: {},
};

export const HIGHLIGHT_ONLY_ALLOWLIST: sanitizeHtml.IOptions = {
  allowedTags: ["mark"],
  allowedAttributes: {},
};

/**
 * Sanitizes a short HTML snippet, such as a highlighted search result, so it
 * can be shown with `v-html`.
 */
export function processSnippet(
  html: string,
  allowlist: sanitizeHtml.IOptions,
): string {
  return sanitizeHtml(html, allowlist);
}

/** Removes all tags from a snippet and keeps its text. */
export function snippetToText(html: string): string {
  return sanitizeHtml(html, { allowedTags: [], allowedAttributes: {} });
}
