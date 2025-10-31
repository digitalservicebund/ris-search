import { vi } from "vitest";
import { formatDocumentKind, formatNormValidity } from "./displayValues";
import { BadgeColor } from "~/components/Badge.vue";
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

  describe("formatNormValidity", () => {
    beforeEach(() => {
      vi.useFakeTimers();
      vi.setSystemTime("2025-01-15");
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    it("returns undefined for empty temporal coverage", () => {
      const result = formatNormValidity("");
      expect(result).toBeUndefined();
    });

    it("returns undefined for invalid temporal coverage", () => {
      const result = formatNormValidity("__invalid__");
      expect(result).toBeUndefined();
    });

    it("returns InForce status with green badge for currently valid norm", () => {
      const result = formatNormValidity("2025-01-01/2025-12-31");
      expect(result).toEqual({
        label: "Aktuell gültig",
        color: BadgeColor.GREEN,
      });
    });

    it("returns InForce status with green badge for norm valid from today", () => {
      const result = formatNormValidity("2025-01-15/..");
      expect(result).toEqual({
        label: "Aktuell gültig",
        color: BadgeColor.GREEN,
      });
    });

    it("returns InForce status with green badge for norm valid until today", () => {
      const result = formatNormValidity("2024-01-01/2025-01-15");
      expect(result).toEqual({
        label: "Aktuell gültig",
        color: BadgeColor.GREEN,
      });
    });

    it("returns InForce status with green badge for norm with open end", () => {
      const result = formatNormValidity("2024-01-01/..");
      expect(result).toEqual({
        label: "Aktuell gültig",
        color: BadgeColor.GREEN,
      });
    });

    it("returns FutureInForce status with yellow badge for norm starting in the future", () => {
      const result = formatNormValidity("2025-02-01/2025-12-31");
      expect(result).toEqual({
        label: "Zukünftig in Kraft",
        color: BadgeColor.YELLOW,
      });
    });

    it("returns Expired status with red badge for norm that ended in the past", () => {
      const result = formatNormValidity("2024-01-01/2024-12-31");
      expect(result).toEqual({
        label: "Außer Kraft",
        color: BadgeColor.RED,
      });
    });

    it("returns Expired status with red badge for norm with open start that ended", () => {
      const result = formatNormValidity("../2024-12-31");
      expect(result).toEqual({
        label: "Außer Kraft",
        color: BadgeColor.RED,
      });
    });

    it("returns undefined for norm with completely open validity period", () => {
      const result = formatNormValidity("../..");
      expect(result).toBeUndefined();
    });
  });
});
