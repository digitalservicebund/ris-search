export function removeOuterParentheses(inputString?: string): string {
  if (!inputString) {
    return "";
  }
  if (inputString.startsWith("(") && inputString.endsWith(")")) {
    return inputString.substring(1, inputString.length - 1);
  } else if (inputString.startsWith("(") && !inputString.includes(")")) {
    return inputString.substring(1, inputString.length);
  } else if (inputString.endsWith(")") && !inputString.includes("(")) {
    return inputString.substring(0, inputString.length - 1);
  }
  return inputString;
}

export function isStringEmpty(string: string | undefined | null): boolean {
  return string === undefined || string === null || string.trim() === "";
}

export function getStringOrDefault(
  string: string | undefined | null,
  defaultValue: string,
): string {
  return isStringEmpty(string) ? defaultValue : (string as string);
}

export function getStringOrUndefined(
  string: string | undefined | null,
): string | undefined {
  return isStringEmpty(string) ? undefined : (string as string);
}

export function addEllipsis(text?: string) {
  if (!text || !text.length) return text;

  const startsWithLowercase = text[0].toUpperCase() !== text[0];
  if (startsWithLowercase) {
    text = "… " + text;
  }

  const sentenceEndsWithPeriod = /[a-z>ß]\.$/g.test(text);
  // match a sentence ending with lowercase character and ".", but not numbered items like "2."
  if (sentenceEndsWithPeriod) {
    return text;
  }
  return text + " …";
}

export function stringToBoolean(
  string: string | undefined,
): boolean | undefined {
  switch (string) {
    case "true":
      return true;
    case "false":
      return false;
    default:
      return undefined;
  }
}

export function normalizeSpaces(text: string): string {
  return text.trim().split(/\s+/).join(" ");
}

export function truncateAtWord(text: string, maxLength: number): string {
  const cleanText = normalizeSpaces(text);
  if (cleanText.length <= maxLength) return cleanText;

  const cut = cleanText.slice(0, maxLength);

  const nextChar = cleanText.charAt(maxLength);
  if (nextChar === "" || nextChar === " ") {
    return cut.trimEnd();
  }

  const lastSpace = cut.lastIndexOf(" ");
  return lastSpace === -1 ? cut : cut.slice(0, lastSpace);
}
