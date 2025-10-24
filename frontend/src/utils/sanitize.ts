import DOMPurify from "isomorphic-dompurify";

export function sanitizeSearchResult(
  html: string,
  allowedTags: string[] | undefined = ["b", "i", "mark"],
) {
  return DOMPurify.sanitize(html, { ALLOWED_TAGS: allowedTags });
}
