import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";

dayjs.extend(utc);
dayjs.extend(timezone);

export function formattedDate(date: string | null | undefined) {
  return date ? dayjs(date).format("DD.MM.YYYY") : undefined;
}

export function splitTemporalCoverage(value: string | null | undefined) {
  if (!value) return [undefined, undefined];
  const [from, to] = value.replaceAll("..", "").split("/");
  return [formattedDate(from), formattedDate(to)];
}

export function isActive(start: string | null, end: string | null): boolean {
  const validDates = start !== null || end !== null;
  const currentDateInGermany = dayjs().tz("Europe/Berlin").startOf("day");
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

export function getCurrentDateInGermany() {
  return dayjs().tz("Europe/Berlin").format("YYYY-MM-DD");
}
