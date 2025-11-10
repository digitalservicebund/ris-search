import { describe, expect } from "vitest";
import type { Literature } from "~/types";

describe("isDocumentEmpty", () => {
  it("returns true if document is undefined", () => {
    const result = isDocumentEmpty(undefined);
    expect(result).toBeTruthy();
  });

  it("returns true if document has no title, outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
    } as Literature);
    expect(result).toBeTruthy();
  });

  it("returns true if document has only headline and no outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
      headline: "headline",
    } as Literature);
    expect(result).toBeTruthy();
  });

  it("returns true if document has only alternativeHeadline and no outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
      alternativeHeadline: "alternativeHeadline",
    } as Literature);
    expect(result).toBeTruthy();
  });

  it("returns true if document has only headlineAdditions and no outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
      headlineAdditions: "headlineAdditions",
    } as Literature);
    expect(result).toBeTruthy();
  });

  it("returns false if document has outline", () => {
    const result = isDocumentEmpty({
      outline: "outline",
    } as Literature);
    expect(result).toBeFalsy();
  });

  it("returns false if document has shortReport", () => {
    const result = isDocumentEmpty({
      shortReport: "shortReport",
    } as Literature);
    expect(result).toBeFalsy();
  });

  it("returns false if document has headline and alternativeHeadline", () => {
    const result = isDocumentEmpty({
      headline: "headline",
      alternativeHeadline: "alternativeHeadline",
    } as Literature);
    expect(result).toBeFalsy();
  });

  it("returns false if document has headline and headlineAdditions", () => {
    const result = isDocumentEmpty({
      headline: "headline",
      headlineAdditions: "headlineAdditions",
    } as Literature);
    expect(result).toBeFalsy();
  });

  it("returns false if document has alternativeHeadline and headlineAdditions", () => {
    const result = isDocumentEmpty({
      alternativeHeadline: "alternativeHeadline",
      headlineAdditions: "headlineAdditions",
    } as Literature);
    expect(result).toBeFalsy();
  });
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
