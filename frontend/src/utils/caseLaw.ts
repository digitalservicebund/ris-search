import type { CaseLaw } from "~/types";

export function getEncodingURL(
  caseLaw: CaseLaw | null | undefined,
  format: string,
) {
  const encoding = caseLaw?.encoding.find((e) => e.encodingFormat === format);
  return encoding?.contentUrl;
}
