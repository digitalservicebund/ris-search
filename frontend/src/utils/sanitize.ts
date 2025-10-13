import DOMPurify from "isomorphic-dompurify";

export function sanitizeSearchResult(
  html: string | undefined,
  allowedTags: string[] | undefined = ["b", "i", "mark"],
) {
  if (!html) {
    return "";
  }

  return DOMPurify.sanitize(html, { ALLOWED_TAGS: allowedTags });
}
