import { describe, vi } from "vitest";
import { getCurrentDateInGermany } from "./dateFormatting";

describe("getCurrentDateInGermany", () => {
  it("returns current date in Germany", () => {
    vi.useFakeTimers();

    // it is 1970-01-01 both in UTC and in German time
    vi.setSystemTime(new Date("1970-01-01T00:00:00Z"));
    expect(getCurrentDateInGermany()).toBe("1970-01-01");

    // it is still 1970-01-01 in UTC, but already past midnight in German time
    vi.setSystemTime(new Date("1970-01-01T23:00:00Z"));
    expect(getCurrentDateInGermany()).toBe("1970-01-02");

    vi.useRealTimers();
  });
});
