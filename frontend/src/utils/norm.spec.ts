import dayjs from "dayjs";
import timezone from "dayjs/plugin/timezone";
import utc from "dayjs/plugin/utc";
import { without } from "lodash-es";
import { describe, expect, vi } from "vitest";
import type { LegislationExpression } from "~/types/api";
import {
  getEinzelnormEIdFromHref,
  getMostRelevantExpression,
  getValidityStatus,
  isNormBodyEmpty,
  temporalCoverageToValidityInterval,
  type ValidityInterval,
} from "~/utils/norm";
import { parseDateGermanLocalTime } from "./dateFormatting";

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

describe("getMostRelevantExpression", () => {
  beforeAll(() => {
    vi.useFakeTimers();
    vi.setSystemTime("2000-01-01");
  });

  afterAll(() => vi.useRealTimers());

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
    const testCase = transform(without(allExpressions, currentExpression));
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
  });

  it("converts empty properties to undefined values", () => {
    const result = getNormMetadataItems({
      abbreviation: undefined,
      legislationIdentifier: "",
      "@type": "Legislation",
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
      hasPart: [],
    });

    expect(result[0]).toMatchObject({ type: "text", value: undefined });
    expect(result[1]).toMatchObject({ type: "badge", values: [] });
    expect(result[2]).toMatchObject({ type: "text", value: undefined });
    expect(result[3]).toMatchObject({ type: "text", value: undefined });
  });

  it("converts properties to correct values", () => {
    const result = getNormMetadataItems({
      abbreviation: "ABC",
      legislationIdentifier: "",
      "@type": "Legislation",
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
      hasPart: [],
    });

    expect(result[0]).toMatchObject({ type: "text", value: "ABC" });
    expect(result[2]).toMatchObject({ type: "text", value: "06.05.2025" });
    expect(result[3]).toMatchObject({ type: "text", value: "31.03.2037" });
  });

  it.each([
    ["1900-01-01/1950-01-01", "Außer Kraft", "red"],
    ["2025-05-06/2037-03-31", "Aktuell gültig", "green"],
    ["2070-01-01/..", "Zukünftig in Kraft", "yellow"],
  ])(
    "displays validity status as badge with color %s and label %s",
    (temporalCoverage, expectedLabel, expectedColor) => {
      const result = getNormMetadataItems({
        abbreviation: "ABC",
        legislationIdentifier: "",
        "@type": "Legislation",
        "@id": "",
        temporalCoverage: temporalCoverage,
        legislationLegalForce: "InForce", // not relevant for validity status calculation
        encoding: [
          {
            "@type": "LegislationObject",
            "@id": "",
            contentUrl: "",
            encodingFormat: "",
            inLanguage: "",
          },
        ],
        hasPart: [],
      });

      expect(result[1]).toMatchObject({
        type: "badge",
        values: [expectedLabel],
        color: expectedColor,
      });
    },
  );
});

describe("isNormBodyEmpty", () => {
  it("returns true if no document is given", () => {
    expect(isNormBodyEmpty(undefined)).toBe(true);
  });

  it("returns true if the document has no akn-body div", () => {
    const doc = new DOMParser().parseFromString("<div></div>", "text/html");
    expect(isNormBodyEmpty(doc)).toBe(true);
  });

  it("returns true if the akn-body div is empty", () => {
    const doc = new DOMParser().parseFromString(
      '<div class="akn-body"></div>',
      "text/html",
    );
    expect(isNormBodyEmpty(doc)).toBe(true);
  });

  it("returns true if the akn-body div contains only whitespaces", () => {
    const doc = new DOMParser().parseFromString(
      '<div class="akn-body">   </div>',
      "text/html",
    );
    expect(isNormBodyEmpty(doc)).toBe(true);
  });

  it("returns false if the akn-body div has content", () => {
    const doc = new DOMParser().parseFromString(
      '<div class="akn-body"><p>Content</p></div>',
      "text/html",
    );
    expect(isNormBodyEmpty(doc)).toBe(false);
  });
});

describe("getEinzelnormEIdFromHref", () => {
  it("extracts the bare eId from an einzelnorm link", () => {
    expect(getEinzelnormEIdFromHref("regelungstext-1/art-z1.html")).toBe(
      "art-z1",
    );
  });

  it("extracts hierarchical eIds", () => {
    expect(
      getEinzelnormEIdFromHref(
        "regelungstext-1/hauptteil-n1_abschnitt-n1_art-z1.html",
      ),
    ).toBe("hauptteil-n1_abschnitt-n1_art-z1");
  });

  it("keeps the eId segment percent-encoded as emitted by the backend", () => {
    expect(
      getEinzelnormEIdFromHref(
        "regelungstext-1/pr%C3%A4ambel-n1_formel-n1.html",
      ),
    ).toBe("pr%C3%A4ambel-n1_formel-n1");
  });

  it("returns null for hash-only links", () => {
    expect(getEinzelnormEIdFromHref("#footnote-1")).toBeNull();
  });

  it("returns null for absolute links", () => {
    expect(getEinzelnormEIdFromHref("https://example.com/foo.html")).toBeNull();
    expect(getEinzelnormEIdFromHref("/gesetze/eli/foo.html")).toBeNull();
  });

  it("returns null for relative links without the .html suffix", () => {
    expect(getEinzelnormEIdFromHref("regelungstext-1/art-z1")).toBeNull();
  });

  it("returns null for links with a query or hash", () => {
    expect(
      getEinzelnormEIdFromHref("regelungstext-1/art-z1.html?foo=bar"),
    ).toBeNull();
    expect(
      getEinzelnormEIdFromHref("regelungstext-1/art-z1.html#frag"),
    ).toBeNull();
  });
});
