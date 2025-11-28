import { describe, expect } from "vitest";
import { getAdministrativeDirectiveMetadataItems } from "./administrativeDirective";

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
