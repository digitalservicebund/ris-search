import type { Dayjs } from "dayjs";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";

dayjs.extend(utc);
dayjs.extend(timezone);

const TIMEZONE_GERMANY = "Europe/Berlin";

export function formattedDate(
  date: string | null | undefined,
): string | undefined {
  return date ? dayjs(date).format("DD.MM.YYYY") : undefined;
}

export function dateFormattedDDMMYYYY(date?: Dayjs): string | undefined {
  return date ? date.format("DD.MM.YYYY") : undefined;
}

export function formattedDateToDateTime(date: string): Date {
  const [day, month, year] = date.split(".").map(Number);
  return new Date(year, month - 1, day);
}

export function parseDateGermanLocalTime(
  dateString: string,
): Dayjs | undefined {
  if (dateString === "") return undefined;
  return dayjs.tz(dateString, TIMEZONE_GERMANY);
}

export function isActive(start: string | null, end: string | null): boolean {
  const validDates = start !== null || end !== null;
  const currentDateInGermany = dayjs().tz(TIMEZONE_GERMANY).startOf("day");
  const hasStarted =
    start === null || !dayjs(start).isAfter(currentDateInGermany);
  const hasNotEnded =
    end === null || !dayjs(end).isBefore(currentDateInGermany);
  return validDates && hasStarted && hasNotEnded;
}

export function getTranslatedLegalForceByDates(
  start: string | null,
  end: string | null,
): string | undefined {
  return translateLegalForce(isActive(start, end) ? "InForce" : "NotInForce");
}

export function translateLegalForce(
  legislationLegalForce?: string,
): string | undefined {
  switch (legislationLegalForce) {
    case "InForce":
      return "In Kraft";
    case "PartiallyInForce":
      return "Teilweise in Kraft";
    case "NotInForce":
      return "Nicht in Kraft";
    default:
      return undefined;
  }
}

export function getCurrentDateInGermany(): Dayjs {
  return dayjs().tz(TIMEZONE_GERMANY);
}

export function getCurrentDateInGermanyFormatted(): string {
  return getCurrentDateInGermany().format("YYYY-MM-DD");
}
