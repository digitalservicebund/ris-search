import { describe, expect } from "vitest";
import {
  getAdministrativeDirectiveDetailItems,
  getAdministrativeDirectiveMetadataItems,
} from "./administrativeDirective";

describe("getAdministrativeDirectiveMetadataItems", () => {
  it("creates correct labels", () => {
    const result = getAdministrativeDirectiveMetadataItems();

    expect(result.map((item) => item.label)).toEqual([
      "Aktenzeichen",
      "Normgeber",
      "Dokumenttyp",
      "Gültig ab",
    ]);

    expect(result[0]).toMatchObject({ type: "badge", values: [] });
    expect(result[1]).toMatchObject({ type: "text", value: undefined });
    expect(result[2]).toMatchObject({ type: "text", value: undefined });
    expect(result[3]).toMatchObject({ type: "text", value: undefined });
  });

  it("maps empty referenceNumbers to empty array", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      referenceNumbers: [],
    });
    expect(result[0]).toMatchObject({ type: "badge", values: [] });
  });

  it("maps single referenceNumber", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      referenceNumbers: ["123"],
    });
    expect(result[0]).toMatchObject({ type: "badge", values: ["123"] });
  });

  it("maps multiple referenceNumbers", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      referenceNumbers: ["123", "456"],
    });
    expect(result[0]).toMatchObject({
      type: "badge",
      values: ["123", "456"],
    });
  });

  it("maps legislationAuthority", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      legislationAuthority: "authority",
    });

    expect(result[1]).toMatchObject({ type: "text", value: "authority" });
  });

  it("maps documentType", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      documentType: "docType",
    });

    expect(result[2]).toMatchObject({ type: "text", value: "docType" });
  });

  it("formats valid entryIntoForceDate", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      entryIntoForceDate: "2025-04-07",
    });

    expect(result[3]).toMatchObject({ type: "text", value: "07.04.2025" });
  });

  it("maps invalid entryIntoForceDate to undefined", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      entryIntoForceDate: "foobar",
    });

    expect(result[3]).toMatchObject({ type: "text", value: undefined });
  });
});

describe("getAdministrativeDirectiveDetailItems", () => {
  it.each([
    [undefined, [], "Fundstelle:"],
    [[], [], "Fundstelle:"],
    [["Foo 1"], ["Foo 1"], "Fundstelle:"],
    [["Foo 1", "Foo 2"], ["Foo 1", "Foo 2"], "Fundstellen:"],
  ])(
    "given references '%o' creates item with value '%s' labeled '%s'",
    (references, expectedValue, expectedLabel) => {
      const result = getAdministrativeDirectiveDetailItems({
        references: references,
      });
      expect(result[0]).toEqual({
        type: "badge",
        label: expectedLabel,
        values: expectedValue,
      });
    },
  );

  it.each([
    [undefined, undefined, "Zitierdatum:"],
    [[], undefined, "Zitierdatum:"],
    [["invalid"], undefined, "Zitierdatum:"],
    [["2025-01-01"], "01.01.2025", "Zitierdatum:"],
    [["2025-01-01", "invalid"], "01.01.2025", "Zitierdatum:"],
    [["2019-07-01", "2025-01-20"], "01.07.2019, 20.01.2025", "Zitierdaten:"],
    [
      ["2019-07-01", "invalid", "2025-01-20"],
      "01.07.2019, 20.01.2025",
      "Zitierdaten:",
    ],
  ])(
    "given citationDates '%o' creates item with value '%s' labeled '%s'",
    (citationDates, expectedValue, expectedLabel) => {
      const result = getAdministrativeDirectiveDetailItems({
        citationDates: citationDates,
      });
      expect(result[1]).toEqual({
        type: "text",
        label: expectedLabel,
        value: expectedValue,
      });
    },
  );

  it.each([
    [undefined, undefined],
    ["invalid", undefined],
    ["2025-01-01", "01.01.2025"],
  ])(
    "given expiryDate '%s' creates \"Gülti bis\" item with value '%s'",
    (expiryDate, expectedValue) => {
      const result = getAdministrativeDirectiveDetailItems({
        expiryDate: expiryDate,
      });
      expect(result[2]).toEqual({
        type: "text",
        label: "Gültig bis:",
        value: expectedValue,
      });
    },
  );

  it.each([
    [undefined, undefined],
    ["FooType", "FooType"],
  ])(
    "given documentTypeDetail '%s' creates \"Dokuemnttyp Zusatz\" item with value '%s'",
    (documentTypeDetail, expectedValue) => {
      const result = getAdministrativeDirectiveDetailItems({
        documentTypeDetail: documentTypeDetail,
      });
      expect(result[3]).toEqual({
        type: "text",
        label: "Dokumenttyp Zusatz:",
        value: expectedValue,
      });
    },
  );

  it.each([
    [undefined, [], "Norm:"],
    [[], [], "Norm:"],
    [["Ref 1"], ["Ref 1"], "Norm:"],
    [["Ref 1", "Ref 2"], ["Ref 1", "Ref 2"], "Normen:"],
  ])(
    "given normReferences '%o' creates item with value '%s' labeled '%s'",
    (normReferenecs, expectedValue, expectedLabel) => {
      const result = getAdministrativeDirectiveDetailItems({
        normReferences: normReferenecs,
      });
      expect(result[4]).toEqual({
        type: "badge",
        label: expectedLabel,
        values: expectedValue,
      });
    },
  );
});
