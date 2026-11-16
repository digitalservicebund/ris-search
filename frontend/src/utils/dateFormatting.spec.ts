import dayjs from "dayjs";
import { describe, vi } from "vitest";
import {
  dateFormattedDDMMYYYY,
  dateFormattedLongGerman,
  getCurrentDateInGermanyFormatted,
} from "./dateFormatting";

describe("getCurrentDateInGermany", () => {
  it("returns current date in Germany", () => {
    vi.useFakeTimers();

    // it is 1970-01-01 both in UTC and in German time
    vi.setSystemTime(new Date("1970-01-01T00:00:00Z"));
    expect(getCurrentDateInGermanyFormatted()).toBe("1970-01-01");

    // it is still 1970-01-01 in UTC, but already past midnight in German time
    vi.setSystemTime(new Date("1970-01-01T23:00:00Z"));
    expect(getCurrentDateInGermanyFormatted()).toBe("1970-01-02");

    vi.useRealTimers();
  });
});

describe("dateFormattedDDMMYYYY", () => {
  it("returns undefined if input is undefined", () => {
    expect(dateFormattedDDMMYYYY()).toBeUndefined();
  });

  it("returns undefined if input not a valid date", () => {
    expect(dateFormattedDDMMYYYY("foo")).toBeUndefined();
  });

  it("returns formatted date if input is valid date string", () => {
    expect(dateFormattedDDMMYYYY("2042-04-02")).toBe("02.04.2042");
  });

  it("returns formatted date if input is dayjs object", () => {
    expect(dateFormattedDDMMYYYY(dayjs("2042-04-02"))).toBe("02.04.2042");
  });
});

describe("dateFormattedLongGerman", () => {
  it("returns undefined if input is undefined", () => {
    expect(dateFormattedLongGerman()).toBeUndefined();
  });

  it("returns undefined if input not a valid date", () => {
    expect(dateFormattedLongGerman("foo")).toBeUndefined();
  });

  it("returns formatted date if input is valid date string", () => {
    expect(dateFormattedLongGerman("2042-03-02")).toBe("2. März 2042");
  });

  it("returns formatted date if input is dayjs object", () => {
    expect(dateFormattedLongGerman(dayjs("2042-12-31"))).toBe(
      "31. Dezember 2042",
    );
  });
});
