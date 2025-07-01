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
  const start = validityInterval?.from ?? dayjs(new Date(-8640000000000000)); // mallest possible date
  const end = validityInterval?.to ?? dayjs(new Date(8640000000000000)); // largest possible date

  if (
    (start.isBefore(end, "day") || start.isSame(end, "day")) &&
    end.isBefore(currentDate, "day")
  ) {
    return ValidityStatus.Historical;
  } else if (
    (start.isBefore(currentDate, "day") || start.isSame(currentDate, "day")) &&
    (end.isSame(currentDate, "day") || end.isAfter(currentDate, "day"))
  ) {
    return ValidityStatus.InForce;
  } else if (
    start.isAfter(currentDate, "day") &&
    (end.isAfter(start, "day") || end.isSame(start, "day"))
  ) {
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
