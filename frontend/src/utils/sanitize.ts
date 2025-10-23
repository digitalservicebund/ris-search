import type DOMPurify from "isomorphic-dompurify";

let _DOMPurify: typeof DOMPurify;

if (import.meta.client) {
  const module = await import("isomorphic-dompurify");
  _DOMPurify = module.default;
}

export function sanitizeSearchResult(
  html: string | undefined,
  allowedTags: string[] = ["b", "i", "mark"],
) {
  if (!import.meta.client || !html || !_DOMPurify) {
    return "";
  }
  return _DOMPurify.sanitize(html, { ALLOWED_TAGS: allowedTags });
}
