import dayjs from "dayjs";
import timezone from "dayjs/plugin/timezone";
import utc from "dayjs/plugin/utc";
import _ from "lodash";
import { describe, expect, vi } from "vitest";
import { parseDateGermanLocalTime } from "./dateFormatting";
import type { LegislationExpression } from "~/types";
import {
  getMostRelevantExpression,
  getValidityStatus,
  temporalCoverageToValidityInterval,
  type ValidityInterval,
} from "~/utils/norm";

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

function createInterval(from?: string, to?: string): ValidityInterval {
  return {
    from: from ? parseDateGermanLocalTime(from) : undefined,
    to: to ? parseDateGermanLocalTime(to) : undefined,
  };
}

describe("getValidityStatus", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  for (const currentDate of [
    "2025-01-01 00:00",
    "2025-01-03 00:00",
    "2025-01-05 00:00",
  ]) {
    it(`returns InForce for current date ${currentDate} and interval 2025-01-01-2025-01-05`, () => {
      setCurrentDate(currentDate);
      const result = getValidityStatus(
        createInterval("2025-01-01", "2025-01-05"),
      );
      expect(result).toBe("InForce");
    });
  }

  it("returns future if start date is after current date", () => {
    setCurrentDate("2024-12-31 23:59");
    const result = getValidityStatus(createInterval("2025-01-01"));
    expect(result).toBe("FutureInForce");
  });

  it("returns historical if end date is before current date", () => {
    setCurrentDate("2025-01-01 00:00");
    const result = getValidityStatus(createInterval(undefined, "2024-12-31"));
    expect(result).toBe("Expired");
  });

  it("returns undefined if start and end date are undefined", () => {
    const result = getValidityStatus();
    expect(result).toBeUndefined();
  });
});

describe("getMostRelevantExpression", () => {
  beforeAll(() => {
    vi.useFakeTimers();
    vi.setSystemTime("2000-01-01");
  });

  afterAll(() => vi.useRealTimers());

  type PartialExpression = Pick<
    LegislationExpression,
    "legislationLegalForce" | "temporalCoverage" | "legislationIdentifier"
  >;

  const currentExpression: PartialExpression = {
    legislationLegalForce: "InForce",
    temporalCoverage: "1999-01-01/..",
    legislationIdentifier: "currentExpression",
  };
  const veryOldExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "1871-05-15/1871-05-17",
    legislationIdentifier: "veryOldExpression",
  };
  const oldExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "1900-01-01/1980-01-01",
    legislationIdentifier: "oldExpression",
  };
  const upcomingExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "2001-01-01/..",
    legislationIdentifier: "upcomingExpression",
  };
  const farFutureExpression: PartialExpression = {
    legislationLegalForce: "NotInForce",
    temporalCoverage: "3000-01-01/..",
    legislationIdentifier: "farFutureExpression",
  };

  function transform(
    partialExpressions: PartialExpression[],
  ): LegislationExpression[] {
    return partialExpressions.map(
      (partialExpression) => partialExpression as LegislationExpression,
    );
  }

  const allExpressions = [
    veryOldExpression,
    oldExpression,
    currentExpression,
    upcomingExpression,
    farFutureExpression,
  ];

  it("picks a current expression if available", () => {
    const testCase = transform(allExpressions);
    expect(getMostRelevantExpression(testCase)).toBe("currentExpression");
  });

  it("picks the nearest future expression if there is no current expression", () => {
    const testCase = transform(_.without(allExpressions, currentExpression));
    expect(getMostRelevantExpression(testCase)).toBe("upcomingExpression");
  });

  it("picks the most recent past expression if there is no current or future expression", () => {
    const testCase = transform([veryOldExpression, oldExpression]);
    expect(getMostRelevantExpression(testCase)).toBe("oldExpression");
  });
});

describe("getNormMetadataItems", () => {
  it("creates correct labels", () => {
    const result = getNormMetadataItems();

    expect(result.map((item) => item.label)).toEqual([
      "Abkürzung",
      "Status",
      "Gültig ab",
      "Gültig bis",
    ]);

    expect(result.map((item) => item.value)).toEqual([
      undefined,
      undefined,
      undefined,
      undefined,
    ]);
  });

  it("converts empty properties to undefined values", () => {
    const result = getNormMetadataItems({
      abbreviation: "",
      legislationIdentifier: "",
      workExample: {
        "@type": "Legislation",
        legislationIdentifier: "",
        "@id": "",
        temporalCoverage: "",
        legislationLegalForce: "NotInForce",
        encoding: [
          {
            "@type": "LegislationObject",
            "@id": "",
            contentUrl: "",
            encodingFormat: "",
            inLanguage: "",
          },
        ],
        tableOfContents: [],
        hasPart: [],
      },
    });

    expect(result.map((item) => item.value)).toEqual([
      "",
      undefined,
      undefined,
      undefined,
    ]);
  });

  it("converts properties to correct values", () => {
    const result = getNormMetadataItems({
      abbreviation: "ABC",
      legislationIdentifier: "",
      workExample: {
        "@type": "Legislation",
        legislationIdentifier: "",
        "@id": "",
        temporalCoverage: "2025-05-06/2037-03-31",
        legislationLegalForce: "NotInForce",
        encoding: [
          {
            "@type": "LegislationObject",
            "@id": "",
            contentUrl: "",
            encodingFormat: "",
            inLanguage: "",
          },
        ],
        tableOfContents: [],
        hasPart: [],
      },
    });

    expect(result.map((item) => item.value)).toEqual([
      "ABC",
      "Aktuell gültig",
      "06.05.2025",
      "31.03.2037",
    ]);
  });
});
