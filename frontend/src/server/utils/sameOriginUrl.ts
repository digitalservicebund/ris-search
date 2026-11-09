/**
 * Resolves the target against the origin and returns it as a URL if it stays on
 * that origin, or undefined otherwise. Use it to validate redirect targets so
 * endpoints can't be used as an open redirect.
 */
export function getSameOriginUrl(target: unknown, origin: string) {
  if (typeof target !== "string") return undefined;

  let url: URL;
  try {
    url = new URL(target, origin);
  } catch {
    return undefined;
  }

  if (url.origin !== origin) return undefined;
  if (url.pathname.startsWith("//")) return undefined;
  return url;
}
