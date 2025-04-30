import DOMPurify from "dompurify";

let purify: typeof DOMPurify;

export function sanitizeSearchResult(
  html: string,
  allowedTags: string[] | undefined = ["b", "i", "mark"],
) {
  if (!purify) {
    if (import.meta.server) {
      const { JSDOM } = require("jsdom");
      const window = new JSDOM("").window;
      purify = DOMPurify(window);
    } else {
      // just use the native DOM on client
      purify = DOMPurify;
    }
  }
  return purify.sanitize(html, { ALLOWED_TAGS: allowedTags });
}
