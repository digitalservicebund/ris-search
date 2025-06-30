import { describe, expect, vi } from "vitest";
import {
  getValidityStatus,
  temporalCoverageToValidityInterval,
} from "~/utils/normUtils";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";

dayjs.extend(utc);
dayjs.extend(timezone);

describe("temporalCoverageToValidityInterval", () => {
  it("returns undefined if temporal coverage is undefined", () => {
    const result = temporalCoverageToValidityInterval(undefined);
    expect(result).toBeUndefined();
  });

  it("extracts from and to date from full temporal coverage string", () => {
    const expectedFrom = parseDateGermanLocalTime("2025-09-01");
    const expectedTo = parseDateGermanLocalTime("2025-12-01");
    const result = temporalCoverageToValidityInterval("2025-09-01/2025-12-01");
    expect(result?.from).toStrictEqual(expectedFrom);
    expect(result?.to).toStrictEqual(expectedTo);
  });

  it("extracts from date from open end temporal coverage string", () => {
    const expectedFrom = parseDateGermanLocalTime("2025-09-01");
    const result = temporalCoverageToValidityInterval("2025-09-01/..");
    expect(result?.from).toStrictEqual(expectedFrom);
    expect(result?.to).toBeUndefined();
  });

  it("extracts to date from open start temporal coverage string", () => {
    const expectedTo = parseDateGermanLocalTime("2025-12-01");
    const result = temporalCoverageToValidityInterval("../2025-12-01");
    expect(result?.from).toBeUndefined();
    expect(result?.to).toStrictEqual(expectedTo);
  });

  it("extracts validity interval from open start and open end temporal coverage string", () => {
    const result = temporalCoverageToValidityInterval("../..");
    expect(result?.from).toBeUndefined();
    expect(result?.to).toBeUndefined();
  });
});

function setCurrentDate(dateTimeString: string) {
  const currentDate = dayjs
    .tz(dateTimeString, "YYYY-MM-DD HH:mm", "Europe/Berlin")
    .toDate();
  vi.setSystemTime(currentDate);
}

describe("expressionStatus", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("returns InForce if current date is in validity interval", () => {
    setCurrentDate("2025-01-03 00:00");
    const result = getValidityStatus(
      parseDateGermanLocalTime("2025-01-01"),
      parseDateGermanLocalTime("2025-01-05"),
    );
    expect(result).toBe(ValidityStatus.InForce);
  });

  it("returns InForce if current date on lower boundary of validity interval", () => {
    setCurrentDate("2025-01-01 00:00");
    const result = getValidityStatus(
      parseDateGermanLocalTime("2025-01-01"),
      parseDateGermanLocalTime("2025-01-05"),
    );
    expect(result).toBe(ValidityStatus.InForce);
  });

  it("returns InForce if current date on upper boundary of validity interval", () => {
    setCurrentDate("2025-01-05 00:00");
    const result = getValidityStatus(
      parseDateGermanLocalTime("2025-01-01"),
      parseDateGermanLocalTime("2025-01-05"),
    );
    expect(result).toBe(ValidityStatus.InForce);
  });

  it("returns future if start date is after current date", () => {
    setCurrentDate("2024-12-31 23:59");
    const result = getValidityStatus(parseDateGermanLocalTime("2025-01-01"));
    expect(result).toBe(ValidityStatus.Future);
  });

  it("returns historical if end date is before current date", () => {
    setCurrentDate("2025-01-01 00:00");
    const result = getValidityStatus(
      undefined,
      parseDateGermanLocalTime("2024-12-31"),
    );
    expect(result).toBe(ValidityStatus.Historical);
  });

  it("returns undefined if start and end date are undefined", () => {
    const result = getValidityStatus();
    expect(result).toBeUndefined();
  });

  it("returns undefined if start date is after end date", () => {
    const result = getValidityStatus(
      parseDateGermanLocalTime("2025-01-01"),
      parseDateGermanLocalTime("2024-12-31"),
    );
    expect(result).toBeUndefined();
  });
});
