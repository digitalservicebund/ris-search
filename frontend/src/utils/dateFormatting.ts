import dayjs from "dayjs";
import type { Dayjs } from "dayjs";
import timezone from "dayjs/plugin/timezone";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);
dayjs.extend(timezone);

const TIMEZONE_GERMANY = "Europe/Berlin";

export function dateFormattedDDMMYYYY(
  date?: Dayjs | string,
): string | undefined {
  const dayjsDate = typeof date === "string" ? dayjs(date) : date;

  if (!dayjsDate?.isValid()) {
    return undefined;
  }

  return dayjsDate.format("DD.MM.YYYY");
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
