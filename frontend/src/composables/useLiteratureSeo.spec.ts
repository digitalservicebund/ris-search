import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useLiteratureSeo } from "./useLiteratureSeo";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useLiteratureSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("buildTitle", () => {
    it("builds a full title from documentType, year, and headline", () => {
      useLiteratureSeo({
        documentTypes: ["Aufsatz"],
        yearsOfPublication: ["2023"],
        headline: "Grundrechte im digitalen Raum",
        alternativeHeadline: "Alternative Überschrift",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023, Grundrechte im digitalen Raum",
        }),
      );
    });

    it("falls back to alternativeHeadline when headline is missing", () => {
      useLiteratureSeo({
        documentTypes: ["Aufsatz"],
        yearsOfPublication: ["2023"],
        alternativeHeadline: "Alternative Überschrift",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023, Alternative Überschrift",
        }),
      );
    });

    it("omits documentType when documentTypes is empty", () => {
      useLiteratureSeo({
        documentTypes: [],
        yearsOfPublication: ["2023"],
        headline: "Grundrechte im digitalen Raum",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "2023, Grundrechte im digitalen Raum",
        }),
      );
    });

    it("omits year when yearsOfPublication is empty", () => {
      useLiteratureSeo({
        documentTypes: ["Aufsatz"],
        yearsOfPublication: [],
        headline: "Grundrechte im digitalen Raum",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, Grundrechte im digitalen Raum",
        }),
      );
    });

    it("omits headline when both headline and alternativeHeadline are missing", () => {
      useLiteratureSeo({
        documentTypes: ["Aufsatz"],
        yearsOfPublication: ["2023"],
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023",
        }),
      );
    });

    it("uses only first documentType and first year when multiple are present", () => {
      useLiteratureSeo({
        documentTypes: ["Aufsatz", "Monografie"],
        yearsOfPublication: ["2023", "2024"],
        headline: "Ein Titel",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023, Ein Titel",
        }),
      );
    });

    it("returns empty string when all fields are missing", () => {
      useLiteratureSeo({
        documentTypes: [],
        yearsOfPublication: [],
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "",
        }),
      );
    });
  });
});
