import { describe, it, expect } from "vitest";
import { getEncodingURL } from "./caseLaw";
import type { CaseLaw } from "~/types";

describe("caseLaw", () => {
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
});
