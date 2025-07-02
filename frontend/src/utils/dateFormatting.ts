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
  return date ? dateFormattedDDMMYYYY(dayjs(date)) : undefined;
}

export function dateFormattedDDMMYYYY(date?: Dayjs): string | undefined {
  return date ? date.format("DD.MM.YYYY") : undefined;
}

export function parseDateGermanLocalTime(
  dateString?: string | null,
): Dayjs | undefined {
  if (!dateString || dateString === "") return undefined;
  return dayjs.tz(dateString, TIMEZONE_GERMANY);
}

export function getCurrentDateInGermany(): Dayjs {
  return dayjs().tz(TIMEZONE_GERMANY);
}

export function getCurrentDateInGermanyFormatted(): string {
  return getCurrentDateInGermany().format("YYYY-MM-DD");
}
