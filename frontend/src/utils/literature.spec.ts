import { describe, expect } from "vitest";
import {
  getLiteratureDetailItems,
  getLiteratureMetadataItems,
  isLiteratureEmpty,
} from "./literature";

describe("isLiteratureEmpty", () => {
  it("returns true if document is undefined", () => {
    const result = isLiteratureEmpty();
    expect(result).toBeTruthy();
  });

  it("returns true if document has no title, outline or short report", () => {
    const result = isLiteratureEmpty({
      outline: null,
      shortReport: null,
    });
    expect(result).toBeTruthy();
  });

  it("returns true if document has only headline and no outline or short report", () => {
    const result = isLiteratureEmpty({
      outline: null,
      shortReport: null,
      headline: "headline",
    });
    expect(result).toBeTruthy();
  });

  it("returns true if document has only alternativeHeadline and no outline or short report", () => {
    const result = isLiteratureEmpty({
      outline: null,
      shortReport: null,
      alternativeHeadline: "alternativeHeadline",
    });
    expect(result).toBeTruthy();
  });

  it("returns true if document has only headlineAdditions and no outline or short report", () => {
    const result = isLiteratureEmpty({
      outline: null,
      shortReport: null,
      headlineAdditions: "headlineAdditions",
    });
    expect(result).toBeTruthy();
  });

  it("returns false if document has outline", () => {
    const result = isLiteratureEmpty({
      outline: "outline",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has shortReport", () => {
    const result = isLiteratureEmpty({
      shortReport: "shortReport",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has headline and alternativeHeadline", () => {
    const result = isLiteratureEmpty({
      headline: "headline",
      alternativeHeadline: "alternativeHeadline",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has headline and headlineAdditions", () => {
    const result = isLiteratureEmpty({
      headline: "headline",
      headlineAdditions: "headlineAdditions",
    });
    expect(result).toBeFalsy();
  });

  it("returns false if document has alternativeHeadline and headlineAdditions", () => {
    const result = isLiteratureEmpty({
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

  it("concatenates dependent and independent references if they exist", () => {
    const result = getLiteratureMetadataItems({
      dependentReferences: ["Dep Ref"],
      independentReferences: ["Indep Ref"],
    });

    expect(result.map((item) => item.value)).toEqual([
      undefined,
      "Dep Ref, Indep Ref",
      undefined,
      undefined,
    ]);
  });
});

describe("getLiteratureDetailsItems", () => {
  it.each([
    [undefined, undefined, "Norm:"],
    [[], undefined, "Norm:"],
    [["Ref 1"], "Ref 1", "Norm:"],
    [["Ref 1", "Ref 2"], "Ref 1, Ref 2", "Normen:"],
  ])(
    "given normReferences '%o' creates item with value '%s' labeled '%s'",
    (normReferences, expectedValue, expectedLabel) => {
      const result = getLiteratureDetailItems({
        normReferences: normReferences,
      });
      expect(result[0]).toEqual({ label: expectedLabel, value: expectedValue });
    },
  );

  it.each([
    [undefined, undefined],
    [[], undefined],
    [["Mustermann, Max"], "Max Mustermann"],
    [
      ["Mustermann, Max", "Musterfrau, Sabine"],
      "Max Mustermann, Sabine Musterfrau",
    ],
  ])(
    "given collaborators '%o' creates \"Mitarbeiter\" item with value '%s'",
    (collaborators, expectedValue) => {
      const result = getLiteratureDetailItems({
        collaborators: collaborators,
      });
      expect(result[1]).toEqual({
        label: "Mitarbeiter:",
        value: expectedValue,
      });
    },
  );

  it.each([
    [undefined, undefined],
    [[], undefined],
    [["Mustermann, Max"], "Max Mustermann"],
    [
      ["Mustermann, Max", "Musterfrau, Sabine"],
      "Max Mustermann, Sabine Musterfrau",
    ],
  ])(
    "given originators '%o' creates \"Urheber\" item with value '%s'",
    (originators, expectedValue) => {
      const result = getLiteratureDetailItems({
        originators: originators,
      });
      expect(result[2]).toEqual({ label: "Urheber:", value: expectedValue });
    },
  );

  it.each([
    [undefined, undefined, "Sprache:"],
    [[], undefined, "Sprache:"],
    [["deu"], "deu", "Sprache:"],
    [["deu", "eng"], "deu, eng", "Sprachen:"],
  ])(
    "given languages '%s' creates item with value '%s' labeled '%s'",
    (languages, expectedValue, expectedLabel) => {
      const result = getLiteratureDetailItems({
        languages: languages,
      });
      expect(result[3]).toEqual({
        label: expectedLabel,
        value: expectedValue,
      });
    },
  );

  it.each([
    [undefined, undefined, "Kongress:"],
    [[], undefined, "Kongress:"],
    [["Note 1"], "Note 1", "Kongress:"],
    [["Note 1", "Note 2"], "Note 1, Note 2", "Kongresse:"],
  ])(
    "given conferenceNotes '%o' creates item with value '%s' labeled '%s'",
    (conferenceNotes, expectedValue, expectedLabel) => {
      const result = getLiteratureDetailItems({
        conferenceNotes: conferenceNotes,
      });
      expect(result[4]).toEqual({ label: expectedLabel, value: expectedValue });
    },
  );
});
