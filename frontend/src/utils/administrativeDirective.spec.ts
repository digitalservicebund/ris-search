import { describe, expect } from "vitest";
import {
  getAdministrativeDirectiveDetailItems,
  getAdministrativeDirectiveMetadataItems,
  isAdministrativeDirectiveEmpty,
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

    expect(result.map((item) => item.value)).toEqual([
      undefined,
      undefined,
      undefined,
      undefined,
    ]);
  });

  it("maps empty referenceNumber to undefined", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      referenceNumbers: [],
    });
    expect(result[0]?.value).toBeUndefined();
  });

  it("maps single referenceNumber", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      referenceNumbers: ["123"],
    });
    expect(result[0]?.value).toBe("123");
  });

  it("maps multiple referenceNumbers", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      referenceNumbers: ["123", "456"],
    });
    expect(result[0]?.value).toBe("123, 456");
  });

  it("maps legislationAuthority", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      legislationAuthority: "authority",
    });

    expect(result[1]?.value).toBe("authority");
  });

  it("maps documentType", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      documentType: "docType",
    });

    expect(result[2]?.value).toBe("docType");
  });

  it("formats valid entryIntoForceDate", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      entryIntoForceDate: "2025-04-07",
    });

    expect(result[3]?.value).toBe("07.04.2025");
  });

  it("maps invalid entryIntoForceDate to undefined", () => {
    const result = getAdministrativeDirectiveMetadataItems({
      entryIntoForceDate: "foobar",
    });

    expect(result[3]?.value).toBeUndefined();
  });
});

describe("isAdministrativeDirectiveEmpty", () => {
  it.each([
    [undefined, undefined, true],
    [undefined, [], true],
    ["", [], true],
    ["", [], true],
    ["foo", undefined, false],
    ["foo", [], false],
    [undefined, ["bar"], false],
    ["", ["bar"], false],
    ["foo", ["bar"], false],
  ])(
    "given shortReport: '%s' and outline: '%o' returns '%s'",
    (shortReport, outline, expected) => {
      expect(
        isAdministrativeDirectiveEmpty({
          shortReport: shortReport,
          outline: outline,
        }),
      ).toBe(expected);
    },
  );
});

describe("getAdministrativeDirectiveDetailItems", () => {
  it.each([
    [undefined, undefined, "Fundstelle:"],
    [[], undefined, "Fundstelle:"],
    [["Foo 1"], "Foo 1", "Fundstelle:"],
    [["Foo 1", "Foo 2"], "Foo 1, Foo 2", "Fundstellen:"],
  ])(
    "given references '%o' creates item with value '%s' labeled '%s'",
    (references, expectedValue, expectedLabel) => {
      const result = getAdministrativeDirectiveDetailItems({
        references: references,
      });
      expect(result[0]).toEqual({ label: expectedLabel, value: expectedValue });
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
      expect(result[2]).toEqual({ label: "Gültig bis:", value: expectedValue });
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
        label: "Dokumenttyp Zusatz:",
        value: expectedValue,
      });
    },
  );

  it.each([
    [undefined, undefined, "Norm:"],
    [[], undefined, "Norm:"],
    [["Ref 1"], "Ref 1", "Norm:"],
    [["Ref 1", "Ref 2"], "Ref 1, Ref 2", "Normen:"],
  ])(
    "given normReferences '%o' creates item with value '%s' labeled '%s'",
    (normReferenecs, expectedValue, expectedLabel) => {
      const result = getAdministrativeDirectiveDetailItems({
        normReferences: normReferenecs,
      });
      expect(result[4]).toEqual({ label: expectedLabel, value: expectedValue });
    },
  );
});
