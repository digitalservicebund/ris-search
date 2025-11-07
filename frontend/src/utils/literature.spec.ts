import { describe, expect } from "vitest";
import type { Literature } from "~/types";

const isDocumentEmptyTestData = [
  ["outline", undefined, undefined],
  ["outline", undefined, "headline"],
  ["outline", "shortReport", undefined],
  ["outline", "shortReport", "headline"],
  [undefined, undefined, "headline"],
  [undefined, "shortReport", undefined],
  [undefined, "shortReport", "headline"],
];

describe("isDocumentEmpty", () => {
  it("returns true if document is undefined", () => {
    const result = isDocumentEmpty(undefined);
    expect(result).toBeTruthy();
  });

  it("returns true if document has neither title, outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
    } as Literature);
    expect(result).toBeTruthy();
  });

  it.each(isDocumentEmptyTestData)(
    "returns false given outline: '%s', shortReport: '%s', headline: '%s'",
    (outline, shortReport, headline) => {
      const result = isDocumentEmpty({
        outline: outline,
        shortReport: shortReport,
        headline: headline,
      } as Literature);
      expect(result).toBeFalsy();
    },
  );
});

const getTitleDataTestData = [
  ["headline1", undefined, undefined, "headline1"],
  ["headline1", undefined, "headline3", "headline1"],
  ["headline1", "headline2", undefined, "headline1"],
  ["headline1", "headline2", "headline3", "headline1"],
  [undefined, "headline2", undefined, "headline2"],
  [undefined, undefined, "headline3", "headline3"],
  [undefined, undefined, undefined, undefined],
  [undefined, "headline2", "headline3", "headline2"],
];

describe("getTitle", () => {
  it("returns undefined if literature is undefined", () => {
    expect(getTitle(undefined)).toBeUndefined();
  });

  it.each(getTitleDataTestData)(
    "given headlines: '%s', '%s', '%s' returns title '%s'",
    (headline, alternativeHeadline, headlineAdditions, expectedTitle) => {
      const result = getTitle({
        headline,
        alternativeHeadline,
        headlineAdditions,
      } as Literature);

      expect(result).toEqual(expectedTitle);
    },
  );
});
