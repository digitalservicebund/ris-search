import { describe, expect, vi } from "vitest";
import {
  getExpressionStatus,
  temporalCoverageToValidityInterval,
} from "~/utils/normUtils";
import type { LegalForceStatus, LegislationExpression } from "~/types";
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

function createLegislationExpression(
  legislationLegalForce: LegalForceStatus,
  temporalCoverage: string,
): LegislationExpression {
  return {
    "@type": "Legislation",
    "@id": "foo",
    legislationIdentifier: "foo",
    temporalCoverage: temporalCoverage,
    legislationLegalForce: legislationLegalForce,
    encoding: [],
    tableOfContents: [],
    hasPart: [],
  };
}

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

  it("returns InForce if legalForce is in force", () => {
    const expression = createLegislationExpression("InForce", "");
    const result = getExpressionStatus(expression);
    expect(result).toBe(ExpressionStatus.InForce);
  });

  it("returns InForce if legalForce is NotInForce but current date is in temporalCoverage", () => {
    setCurrentDate("2025-01-01 00:00");
    const expression = createLegislationExpression(
      "NotInForce",
      "2025-01-01/2025-01-01",
    );
    const result = getExpressionStatus(expression);
    expect(result).toBe(ExpressionStatus.InForce);
  });

  it("returns future if legalForce is NotInForce and start date is in future", () => {
    setCurrentDate("2024-12-31 23:59");
    const expression = createLegislationExpression(
      "NotInForce",
      "2025-01-01/..",
    );
    const result = getExpressionStatus(expression);
    expect(result).toBe(ExpressionStatus.Future);
  });

  it("returns historical if legalForce is NotInForce and end date is in past", () => {
    setCurrentDate("2025-01-01 00:00");
    const expression = createLegislationExpression(
      "NotInForce",
      "2024-09-01/2024-12-31",
    );
    const result = getExpressionStatus(expression);
    expect(result).toBe(ExpressionStatus.Historical);
  });

  it("returns undefined if legalForce is partiallyInForce", () => {
    const expression = createLegislationExpression("PartiallyInForce", "");
    const result = getExpressionStatus(expression);
    expect(result).toBeUndefined();
  });
});
