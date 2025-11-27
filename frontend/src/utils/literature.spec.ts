import { describe, expect } from "vitest";
import { getLiteratureMetadataItems } from "./literature";

describe("isDocumentEmpty", () => {
  it("returns true if document is undefined", () => {
    const result = isDocumentEmpty();
    expect(result).toBeTruthy();
  });

  it("returns true if document has no title, outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
    });
    expect(result).toBeTruthy();
  });

  it("returns true if document has only headline and no outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
      headline: "headline",
    });
    expect(result).toBeTruthy();
  });

  it("returns true if document has only alternativeHeadline and no outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
      alternativeHeadline: "alternativeHeadline",
    });
    expect(result).toBeTruthy();
  });

  it("returns true if document has only headlineAdditions and no outline or short report", () => {
    const result = isDocumentEmpty({
      outline: null,
      shortReport: null,
      headlineAdditions: "headlineAdditions",
    });
    expect(result).toBeTruthy();
  });

  it("returns false if document has outline", () => {
    const result = isDocumentEmpty({
      outline: "outline",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has shortReport", () => {
    const result = isDocumentEmpty({
      shortReport: "shortReport",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has headline and alternativeHeadline", () => {
    const result = isDocumentEmpty({
      headline: "headline",
      alternativeHeadline: "alternativeHeadline",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has headline and headlineAdditions", () => {
    const result = isDocumentEmpty({
      headline: "headline",
      headlineAdditions: "headlineAdditions",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has alternativeHeadline and headlineAdditions", () => {
    const result = isDocumentEmpty({
      alternativeHeadline: "alternativeHeadline",
      headlineAdditions: "headlineAdditions",
    });
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
    expect(getTitle()).toBeUndefined();
  });

  it.each(getTitleDataTestData)(
    "given headlines: '%s', '%s', '%s' returns title '%s'",
    (headline, alternativeHeadline, headlineAdditions, expectedTitle) => {
      const result = getTitle({
        headline,
        alternativeHeadline,
        headlineAdditions,
      });

      expect(result).toEqual(expectedTitle);
    },
  );
});

describe("getLiteratureMetadataItems", () => {
  it("creates correct labels", () => {
    const result = getLiteratureMetadataItems();

    expect(result.map((item) => item.label)).toEqual([
      "Dokumenttyp",
      "Fundstelle",
      "Autor",
      "Veröffentlichungsjahr",
    ]);

    expect(result.map((item) => item.value)).toEqual([
      undefined,
      undefined,
      undefined,
      undefined,
    ]);
  });

  it("converts empty properties to undefined values", () => {
    const result = getLiteratureMetadataItems({
      documentTypes: [],
      dependentReferences: [],
      authors: [],
      yearsOfPublication: [],
    });

    expect(result.map((item) => item.value)).toEqual([
      undefined,
      undefined,
      undefined,
      undefined,
    ]);
  });

  it("converts properties with one value", () => {
    const result = getLiteratureMetadataItems({
      documentTypes: ["Foo"],
      dependentReferences: ["Ref"],
      authors: ["Mustermann, Max"],
      yearsOfPublication: ["2015"],
    });

    expect(result.map((item) => item.value)).toEqual([
      "Foo",
      "Ref",
      "Max Mustermann",
      "2015",
    ]);
  });

  it("converts properties with multiple values", () => {
    const result = getLiteratureMetadataItems({
      documentTypes: ["Foo", "Bar"],
      dependentReferences: ["Ref1", "Ref2"],
      authors: ["Mustermann, Max", "Musterfrau, Sabine"],
      yearsOfPublication: ["2015", "2016"],
    });

    expect(result.map((item) => item.value)).toEqual([
      "Foo, Bar",
      "Ref1, Ref2",
      "Max Mustermann, Sabine Musterfrau",
      "2015, 2016",
    ]);
  });
});
