import type { LegislationWork } from "~/types";

export function getManifestationUrl(
  metadata: LegislationWork | undefined,
  backendURL: string,
  format: string,
) {
  const encoding = metadata?.workExample?.encoding.find(
    (e) => e.encodingFormat === format,
  );
  return encoding?.contentUrl ? backendURL + encoding.contentUrl : undefined;
}
