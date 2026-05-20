import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useAdministrativeDirectiveSeo } from "./useAdministrativeDirectiveSeo";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useAdministrativeDirectiveSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("buildTitle", () => {
    it("builds title from documentType, entryIntoForceDate, and headline", () => {
      useAdministrativeDirectiveSeo({
        documentType: "Verwaltungsvorschrift",
        entryIntoForceDate: "2023-01-15",
        headline: "Richtlinie zur Datenverwaltung",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title:
            "Verwaltungsvorschrift vom 15.01.2023, Richtlinie zur Datenverwaltung",
        }),
      );
    });

    it("uses fallback when documentType is missing", () => {
      useAdministrativeDirectiveSeo({
        entryIntoForceDate: "2023-01-15",
        headline: "Richtlinie zur Datenverwaltung",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title:
            "Verwaltungsvorschrift vom 15.01.2023, Richtlinie zur Datenverwaltung",
        }),
      );
    });

    it("omits date when entryIntoForceDate is missing", () => {
      useAdministrativeDirectiveSeo({
        documentType: "Verwaltungsvorschrift",
        headline: "Richtlinie zur Datenverwaltung",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Verwaltungsvorschrift, Richtlinie zur Datenverwaltung",
        }),
      );
    });

    it("omits headline when it is missing", () => {
      useAdministrativeDirectiveSeo({
        documentType: "Verwaltungsvorschrift",
        entryIntoForceDate: "2023-01-15",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Verwaltungsvorschrift vom 15.01.2023",
        }),
      );
    });

    it("returns fallback when all fields are missing", () => {
      useAdministrativeDirectiveSeo({});

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Verwaltungsvorschrift",
        }),
      );
    });
  });
});
