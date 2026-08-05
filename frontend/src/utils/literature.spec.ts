import { describe, expect } from "vitest";
import {
  getLiteratureDetailItems,
  getLiteratureMetadataItems,
} from "./literature";

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
      expect(result[0]).toEqual({
        type: "text",
        label: expectedLabel,
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
    "given collaborators '%o' creates \"Mitarbeiter\" item with value '%s'",
    (collaborators, expectedValue) => {
      const result = getLiteratureDetailItems({
        collaborators: collaborators,
      });
      expect(result[1]).toEqual({
        type: "text",
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
      expect(result[2]).toEqual({
        type: "text",
        label: "Urheber:",
        value: expectedValue,
      });
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
        type: "text",
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
      expect(result[4]).toEqual({
        type: "text",
        label: expectedLabel,
        value: expectedValue,
      });
    },
  );

  it("returns sli details for sli documents with singular properties", () => {
    const result = getLiteratureDetailItems({
      inLanguage: "de",
      documentNumber: "LIT-123",
      normReferences: ["GG, Art 6 Abs 2 S 1, 1949-05-23"],
      collaborators: ["Doe, John", "Doe, Jane"],
      founder: ["Doe, Founder"],
      editors: ["Doe, Editor"],
      originators: ["FOO"],
      publishers: ["Doe, Publisher"],
      publisherOrganizations: ["Institution"],
      publishingHouses: ["Nomos, Baden-Baden"],
      edition: "first edition",
      volumes: ["Teilband 1"],
      conferenceNotes: ["Internationaler Kongress 2025, Berlin, GER"],
      languages: ["deu"],
      universityNotes: ["University"],
      literatureType: "sli",
      internationalIdentifiers: ["ISBN-XXXX"],
    });

    expect(new Set(result)).toEqual(
      new Set([
        {
          type: "text",
          label: "Norm:",
          value: "GG, Art 6 Abs 2 S 1, 1949-05-23",
        },
        { type: "text", label: "Bearbeiter:", value: "Editor Doe" },
        { type: "text", label: "Mitarbeiter:", value: "John Doe, Jane Doe" },
        { type: "text", label: "Urheber:", value: "FOO" },
        { type: "text", label: "Begründer:", value: "Founder Doe" },
        {
          type: "text",
          label: "Herausgeber:",
          value: "Institution, Publisher Doe",
        },
        { type: "text", label: "Verlag:", value: "Nomos, Baden-Baden" },
        { type: "text", label: "Ausgabe:", value: "first edition" },
        { type: "text", label: "Bestellnummer:", value: "ISBN-XXXX" },
        { type: "list", label: "Teilband:", values: ["Teilband 1"] },
        { type: "text", label: "Sprache:", value: "deu" },
        {
          type: "text",
          label: "Kongress:",
          value: "Internationaler Kongress 2025, Berlin, GER",
        },
        { type: "text", label: "Hochschule:", value: "University" },
      ]),
    );
  });

  it("returns sli details for sli documents and transforms plural properties", () => {
    const result = getLiteratureDetailItems({
      inLanguage: "de",
      documentNumber: "LIT-123",
      normReferences: [
        "GG, Art 6 Abs 2 S 1, 1949-05-23",
        "GG, Art 4 Abs 3 S 1, 1949-05-23",
      ],
      collaborators: ["Doe, John", "Doe, Jane"],
      founder: ["Doe, Founder"],
      editors: ["Doe, Editor"],
      originators: ["FOO"],
      publishers: ["Doe, Publisher"],
      publisherOrganizations: ["Institution"],
      publishingHouses: ["Nomos, Baden-Baden"],
      edition: "first edition",
      volumes: ["Teilband 1", "Teilband 2"],
      conferenceNotes: [
        "Internationaler Kongress 2025, Berlin, GER",
        "Kongress 2",
      ],
      languages: ["deu", "eng"],
      universityNotes: ["University 1", "University 2"],
      literatureType: "sli",
      internationalIdentifiers: ["ISBN-XXXX"],
    });

    expect(new Set(result)).toEqual(
      new Set([
        {
          type: "text",
          label: "Normen:",
          value:
            "GG, Art 6 Abs 2 S 1, 1949-05-23, GG, Art 4 Abs 3 S 1, 1949-05-23",
        },
        { type: "text", label: "Bearbeiter:", value: "Editor Doe" },
        { type: "text", label: "Mitarbeiter:", value: "John Doe, Jane Doe" },
        { type: "text", label: "Urheber:", value: "FOO" },
        { type: "text", label: "Begründer:", value: "Founder Doe" },
        {
          type: "text",
          label: "Herausgeber:",
          value: "Institution, Publisher Doe",
        },
        { type: "text", label: "Verlag:", value: "Nomos, Baden-Baden" },
        { type: "text", label: "Ausgabe:", value: "first edition" },
        { type: "text", label: "Bestellnummer:", value: "ISBN-XXXX" },
        {
          type: "list",
          label: "Teilband:",
          values: ["Teilband 1", "Teilband 2"],
        },
        { type: "text", label: "Sprachen:", value: "deu, eng" },
        {
          type: "text",
          label: "Kongresse:",
          value: "Internationaler Kongress 2025, Berlin, GER, Kongress 2",
        },
        {
          type: "text",
          label: "Hochschulen:",
          value: "University 1, University 2",
        },
      ]),
    );
  });

  it("handles empty values in sli details", () => {
    const result = getLiteratureDetailItems({
      literatureType: "sli",
    });

    expect(new Set(result)).toEqual(
      new Set([
        { type: "text", label: "Norm:", value: undefined },
        { type: "text", label: "Bearbeiter:", value: undefined },
        { type: "text", label: "Mitarbeiter:", value: undefined },
        { type: "text", label: "Urheber:", value: undefined },
        { type: "text", label: "Begründer:", value: undefined },
        { type: "text", label: "Herausgeber:", value: undefined },
        { type: "text", label: "Verlag:", value: undefined },
        { type: "text", label: "Ausgabe:", value: undefined },
        { type: "text", label: "Bestellnummer:", value: undefined },
        { type: "list", label: "Teilband:", values: [] },
        { type: "text", label: "Sprache:", value: undefined },
        { type: "text", label: "Kongress:", value: undefined },
        { type: "text", label: "Hochschule:", value: undefined },
      ]),
    );
  });

  it("merges publishers and publisherOrganizations", () => {
    const result = getLiteratureDetailItems({
      literatureType: "sli",
      publishers: ["Doe, Publisher1", "Doe, Publisher2"],
      publisherOrganizations: ["Institution1", "Institution2"],
    });

    const herausgeberItem = result.find(
      (item) => item.label === "Herausgeber:",
    );
    expect(herausgeberItem).toMatchObject({
      value: "Institution1, Institution2, Publisher1 Doe, Publisher2 Doe",
    });
  });

  it("handles only publishers when publisherOrganizations is empty", () => {
    const result = getLiteratureDetailItems({
      literatureType: "sli",
      publishers: ["Doe, Publisher"],
      publisherOrganizations: [],
    });

    const herausgeberItem = result.find(
      (item) => item.label === "Herausgeber:",
    );
    expect(herausgeberItem).toMatchObject({ value: "Publisher Doe" });
  });

  it("handles only publisherOrganizations when publishers is empty", () => {
    const result = getLiteratureDetailItems({
      literatureType: "sli",
      publishers: [],
      publisherOrganizations: ["Institution"],
    });

    const herausgeberItem = result.find(
      (item) => item.label === "Herausgeber:",
    );
    expect(herausgeberItem).toMatchObject({ value: "Institution" });
  });
});
