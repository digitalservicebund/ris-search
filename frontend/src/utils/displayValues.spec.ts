import { formatDocumentKind } from "./displayValues";
import { DocumentKind } from "~/types";

describe("displayValues", () => {
  describe("formatDocumentKind", () => {
    it("formats caselaw", () => {
      expect(formatDocumentKind(DocumentKind.CaseLaw)).toBe(
        "Gerichtsentscheidungen",
      );
    });

    it("formats norms", () => {
      expect(formatDocumentKind(DocumentKind.Norm)).toBe(
        "Gesetze & Verordnungen",
      );
    });

    it("formats literature", () => {
      expect(formatDocumentKind(DocumentKind.Literature)).toBe(
        "Literaturnachweise",
      );
    });

    it("formats all", () => {
      expect(formatDocumentKind(DocumentKind.All)).toBe("Datensätze");
    });

    it("returns all as the fallback", () => {
      // @ts-expect-error -- Intentionally breaking for testing purposes
      expect(formatDocumentKind(null)).toBe("Datensätze");
    });
  });
});
