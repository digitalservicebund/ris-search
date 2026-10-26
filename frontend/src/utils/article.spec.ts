import { describe, expect, it } from "vitest";
import type { LegislationExpression } from "~/types/api";
import { getNewestExpression, tocHeadlineAdditionLabel } from "~/utils/article";

describe("tocHeadlineAdditionLabel", () => {
  it("returns an empty string if temporal coverage is undefined", () => {
    expect(tocHeadlineAdditionLabel(undefined)).toBe("");
  });

  it("returns an empty string for an open start and open end temporal coverage", () => {
    expect(tocHeadlineAdditionLabel("../..")).toBe("");
  });

  it("returns from and to date for a full temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("2025-09-01/2025-12-01")).toBe(
      "(01.09.2025 - 01.12.2025)",
    );
  });

  it("returns only the from date for an open end temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("2025-09-01/..")).toBe("(vom 01.09.2025)");
  });

  it("returns only the to date for an open start temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("../2025-12-01")).toBe("(bis 01.12.2025)");
  });
});

function expression(temporalCoverage: string): LegislationExpression {
  return { temporalCoverage } as LegislationExpression;
}

describe("getNewestExpression", () => {
  it("returns undefined for an empty list", () => {
    expect(getNewestExpression([])).toBeUndefined();
  });

  it("returns the expression whose validity period starts last", () => {
    const newest = expression("2022-01-01/..");
    expect(
      getNewestExpression([
        expression("2021-01-01/2021-12-31"),
        newest,
        expression("2020-01-01/2020-12-31"),
      ]),
    ).toBe(newest);
  });

  it("prefers expressions with a start date over those without", () => {
    const withStart = expression("2020-01-01/..");

    expect(getNewestExpression([expression("../2030-01-01"), withStart])).toBe(
      withStart,
    );
  });
});
