import sanitizeHtml from "sanitize-html";

export function sanitizeSearchResult(
  html: string,
  allowedTags: string[] | undefined = ["b", "i", "mark"],
) {
  return sanitizeHtml(html, { allowedTags: allowedTags });
}

/** Strips all HTML tags from the given string, returning plain text. */
export function stripAllHtml(html: string): string {
  return sanitizeHtml(html, { allowedTags: [] });
}
