import { parseDateGermanLocalTime } from "~/utils/dateFormatting";
import type { LegislationWork } from "~/types";
import dayjs, { type Dayjs } from "dayjs";

export interface ValidityInterval {
  from?: Dayjs;
  to?: Dayjs;
}

export function temporalCoverageToValidityInterval(
  temporalCoverage: string | null | undefined,
): ValidityInterval | undefined {
  if (!temporalCoverage) return undefined;
  const [from, to] = temporalCoverage.replaceAll("..", "").split("/");
  return {
    from: parseDateGermanLocalTime(from),
    to: parseDateGermanLocalTime(to),
  };
}

export enum ValidityStatus {
  InForce = "Aktuell gültig",
  Future = "Zukünftig in Kraft",
  Historical = "Außer Kraft",
}

export function getValidityStatus(
  validityInterval?: ValidityInterval,
): ValidityStatus | undefined {
  if (!validityInterval?.from && !validityInterval?.to) return undefined;

  const currentDate = getCurrentDateInGermany();
  const start = validityInterval?.from ?? dayjs(new Date("0000-01-01"));
  const end = validityInterval?.to ?? dayjs(new Date("9999-12-31"));

  if (end.isBefore(currentDate, "day")) {
    return ValidityStatus.Historical;
  }

  if (
    (start.isBefore(currentDate, "day") || start.isSame(currentDate, "day")) &&
    (end.isSame(currentDate, "day") || end.isAfter(currentDate, "day"))
  ) {
    return ValidityStatus.InForce;
  }

  if (start.isAfter(currentDate, "day")) {
    return ValidityStatus.Future;
  }

  return undefined;
}

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
