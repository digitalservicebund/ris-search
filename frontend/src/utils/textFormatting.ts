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
  if (!text?.length) return text;

  const startsWithLowercase = text[0]?.toUpperCase() !== text[0];
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

export function removePrefix(str: string | undefined, prefix: string) {
  if (str?.trimStart().startsWith(prefix)) {
    return str.substring(str.indexOf(prefix) + prefix.length).trimStart();
  }

  return str;
}

/**
 * Returns a string constructed by joining the array elements with ', '.
 * If the array only contains one element, the element is returned.
 * Returns undefined if the array is empty
 * @param array
 */
export function formatArray(array: string[]): string | undefined {
  if (array.length == 0) {
    return undefined;
  }

  return array.join(", ");
}

/**
 * Expects an array of strings where each element is a name in the format
 * "Lastname, Firstname". The names are converted to "Firstname Lastname".
 * If an element contains no or more than one comma, it is kept unchanged.
 * @param names
 */
export function formatNames(names: string[]): string[] {
  return names.map((name) => {
    const parts = name.split(",");
    if (parts.length == 2) {
      const lastname = parts[0]?.trim();
      const firstname = parts[1]?.trim();
      return `${firstname} ${lastname}`;
    } else {
      return name;
    }
  });
}

/**
 * Encodes German umlauts into ASCII-friendly equivalents for URI usage.
 * Mirrors the logic used in XSLT:
 *  ä → ae, ö → oe, ü → ue (and uppercase versions)
 */
export function encodeForUri(text?: string): string {
  if (!text) return "";

  const map: Record<string, string> = {
    ä: "ae",
    ö: "oe",
    ü: "ue",
    Ä: "Ae",
    Ö: "Oe",
    Ü: "Ue",
  };

  return text.replaceAll(/[äöüÄÖÜ]/g, (char: string) => map[char] ?? char);
}

/**
 * Given a noun in singular and plural form, choses the correct one based on the provided count.
 * @param singular
 * @param plural
 * @param count
 */
export function getSingularOrPlural(
  singular: string,
  plural: string,
  count?: number,
) {
  if ((count ?? 0) > 1) return plural;
  return singular;
}
