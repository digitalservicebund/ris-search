import sanitizeHtml from "sanitize-html";

export function sanitizeSearchResult(
  html: string,
  allowedTags: string[] | undefined = ["b", "i", "mark"],
) {
  return sanitizeHtml(html, { allowedTags: allowedTags });
}
