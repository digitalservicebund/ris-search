import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { Literature } from "~/types/api";
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
        literature: {
          documentTypes: ["Aufsatz"],
          yearsOfPublication: ["2023"],
          headline: "Grundrechte im digitalen Raum",
          alternativeHeadline: "Alternative Überschrift",
        } as Literature,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023, Grundrechte im digitalen Raum",
        }),
      );
    });

    it("falls back to alternativeHeadline when headline is missing", () => {
      useLiteratureSeo({
        literature: {
          documentTypes: ["Aufsatz"],
          yearsOfPublication: ["2023"],
          alternativeHeadline: "Alternative Überschrift",
        } as Literature,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023, Alternative Überschrift",
        }),
      );
    });

    it("omits documentType when documentTypes is empty", () => {
      useLiteratureSeo({
        literature: {
          documentTypes: [] as string[],
          yearsOfPublication: ["2023"],
          headline: "Grundrechte im digitalen Raum",
        } as Literature,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "2023, Grundrechte im digitalen Raum",
        }),
      );
    });

    it("omits year when yearsOfPublication is empty", () => {
      useLiteratureSeo({
        literature: {
          documentTypes: ["Aufsatz"],
          yearsOfPublication: [] as string[],
          headline: "Grundrechte im digitalen Raum",
        } as Literature,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, Grundrechte im digitalen Raum",
        }),
      );
    });

    it("omits headline when both headline and alternativeHeadline are missing", () => {
      useLiteratureSeo({
        literature: {
          documentTypes: ["Aufsatz"],
          yearsOfPublication: ["2023"],
        } as Literature,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023",
        }),
      );
    });

    it("uses only first documentType and first year when multiple are present", () => {
      useLiteratureSeo({
        literature: {
          documentTypes: ["Aufsatz", "Monografie"],
          yearsOfPublication: ["2023", "2024"],
          headline: "Ein Titel",
        } as Literature,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Aufsatz, 2023, Ein Titel",
        }),
      );
    });

    it("returns empty string when all fields are missing", () => {
      useLiteratureSeo({
        literature: {
          documentTypes: [] as string[],
          yearsOfPublication: [] as string[],
        } as Literature,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "",
        }),
      );
    });
  });
});
