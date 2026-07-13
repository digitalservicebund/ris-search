import { describe, expect, it } from "vitest";
import type { CaseLaw } from "~/types/api";
import { getCaselawSecondaryTitle, getEncodingURL } from "~/utils/caseLaw";

describe("caselaw", () => {
  describe("getEncodingURL", () => {
    const mockCaseLaw: CaseLaw = {
      "@type": "Decision",
      "@id": "test-id",
      documentNumber: "TEST-123",
      ecli: "ECLI:TEST:123",
      decisionDate: "2024-01-01",
      fileNumbers: [],
      keywords: [],
      decisionName: [],
      deviatingDocumentNumber: [],
      inLanguage: "de",
      encoding: [
        {
          "@type": "DecisionObject",
          "@id": "enc-1",
          contentUrl: "https://example.com/doc.pdf",
          encodingFormat: "application/pdf",
          inLanguage: "de",
        },
        {
          "@type": "DecisionObject",
          "@id": "enc-2",
          contentUrl: "https://example.com/doc.xml",
          encodingFormat: "application/xml",
          inLanguage: "de",
        },
      ],
    };

    it("returns the URL for a matching format", () => {
      expect(getEncodingURL(mockCaseLaw, "application/pdf")).toBe(
        "https://example.com/doc.pdf",
      );
      expect(getEncodingURL(mockCaseLaw, "application/xml")).toBe(
        "https://example.com/doc.xml",
      );
    });

    it("returns undefined for non-matching format", () => {
      expect(getEncodingURL(mockCaseLaw, "text/html")).toBeUndefined();
    });

    it("returns undefined for null/undefined caseLaw", () => {
      expect(getEncodingURL(null, "application/pdf")).toBeUndefined();
      expect(getEncodingURL(undefined, "application/pdf")).toBeUndefined();
    });
  });

  describe("getCaselawSecondaryTitle", () => {
    it("prefers the first nonblank decision name", () => {
      expect(
        getCaselawSecondaryTitle({
          decisionName: ["", "Decision name"],
          titleLine: "Title line",
        }),
      ).toBe("Decision name");
    });

    it("falls back to the Titlezeile", () => {
      expect(
        getCaselawSecondaryTitle({ decisionName: [], titleLine: "Title line" }),
      ).toBe("Title line");
    });

    it("returns undefined without a decision name or Titlezeile", () => {
      expect(getCaselawSecondaryTitle({ decisionName: [] })).toBeUndefined();
    });

    it("truncates the secondary title to 90 characters", () => {
      expect(
        getCaselawSecondaryTitle({ decisionName: ["a".repeat(100)] }),
      ).toBe("a".repeat(90));
    });
  });
});
