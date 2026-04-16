export function prependBase(url: string, base: string) {
  if (url.startsWith("http")) {
    return url;
  }
  base = base.endsWith("/") ? base.slice(0, -1) : base;
  return url.startsWith("/") ? `${base}${url}` : `${base}/${url}`;
}

export function removeBase(url: string, base: string) {
  return url.startsWith("http")
    ? url.replace(base, "")
    : ensureStartingSlash(url.replace(base, ""));
}

export function ensureStartingSlash(path: string): string {
  return path.startsWith("/") ? path : `/${path}`;
}

export function slugify(...parts: string[]) {
  return parts
    .map((s) => s.toLocaleLowerCase().replaceAll(/[\-\s]+/g, "-"))
    .filter(Boolean)
    .join("-");
}

export const HASH_REGEXP = /#.*$/;
export const EXT_REGEXP = /(index)?\.(md|html)$/;
export const inBrowser = typeof document !== "undefined";

export function isActive(
  currentPath: string,
  matchPath?: string,
  strict: boolean = true
): boolean {
  if (matchPath === undefined) {
    return false;
  }

  const normalizedCurrentPath = normalize(currentPath);
  const normalizedMatchPath = normalize(matchPath);

  if (strict && normalizedMatchPath === normalizedCurrentPath) {
    return true;
  } else if (!strict && normalizedCurrentPath.startsWith(normalizedMatchPath)) {
    return true;
  } else {
    return false;
  }
}

export function normalize(path: string): string {
  return ensureStartingSlash(
    decodeURI(path).replace(HASH_REGEXP, "").replace(EXT_REGEXP, "")
  );
}

export function isStringEmpty(string: string | undefined | null): boolean {
  return string === undefined || string === null || string.trim() === "";
}
