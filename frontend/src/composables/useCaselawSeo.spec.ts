import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { CaseLaw } from "~/types/api";
import { useCaselawSeo } from "./useCaselawSeo";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useCaselawSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("falls back to defaults when caseLaw and document is undefined", () => {
    useCaselawSeo({});

    expect(useSeo).toHaveBeenCalledWith(
      expect.objectContaining({
        title: "Gerichtsentscheidung",
        description: "Gerichtsentscheidung",
        ogTitle: "Gerichtsentscheidung",
      }),
    );
  });

  describe("buildTitle", () => {
    it("builds a full title from court, documentType, date, and fileNumber", () => {
      useCaselawSeo({
        caseLaw: {
          courtName: "Bundesgerichtshof",
          documentType: "Urteil",
          decisionDate: "2023-06-15",
          fileNumbers: ["VIII ZR 12/23"],
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Bundesgerichtshof, Urteil vom 15.06.2023 - VIII ZR 12/23",
        }),
      );
    });

    it("falls back to 'Gerichtsentscheidung' when documentType is missing", () => {
      useCaselawSeo({
        caseLaw: {
          courtName: "Bundesgerichtshof",
          decisionDate: "2023-06-15",
          fileNumbers: ["VIII ZR 12/23"],
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title:
            "Bundesgerichtshof, Gerichtsentscheidung vom 15.06.2023 - VIII ZR 12/23",
        }),
      );
    });

    it("omits court when courtName is missing", () => {
      useCaselawSeo({
        caseLaw: {
          documentType: "Urteil",
          decisionDate: "2023-06-15",
          fileNumbers: ["VIII ZR 12/23"],
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Urteil vom 15.06.2023 - VIII ZR 12/23",
        }),
      );
    });

    it("omits date when decisionDate is missing", () => {
      useCaselawSeo({
        caseLaw: {
          courtName: "Bundesgerichtshof",
          documentType: "Urteil",
          fileNumbers: ["VIII ZR 12/23"],
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Bundesgerichtshof, Urteil - VIII ZR 12/23",
        }),
      );
    });

    it("omits fileNumber when fileNumbers is undefined", () => {
      useCaselawSeo({
        caseLaw: {
          courtName: "Bundesgerichtshof",
          documentType: "Urteil",
          decisionDate: "2023-06-15",
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Bundesgerichtshof, Urteil vom 15.06.2023",
        }),
      );
    });

    it("uses first of multiple fileNumbers", () => {
      useCaselawSeo({ caseLaw: { fileNumbers: ["X1", "X2"] } as CaseLaw });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Gerichtsentscheidung - X1",
        }),
      );
    });
  });

  describe("buildDescription", () => {
    it("uses guidingPrinciple's first two sentences", () => {
      const doc = new DOMParser().parseFromString(
        "<html lang='de'><body><section><p>Not used in this case.</p></section></body></html>",
        "text/html",
      );
      useCaselawSeo({
        caseLaw: {
          guidingPrinciple:
            "Fist sentence. Second sentence. Third sentence should be cut off.",
        } as CaseLaw,
        document: doc,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          description: "Fist sentence. Second sentence.",
        }),
      );
    });

    it("falls back to first paragraph from document when guidingPrinciple missing", () => {
      const doc = new DOMParser().parseFromString(
        "<html lang='de'><body><section><p>Paragraph text here.</p></section></body></html>",
        "text/html",
      );
      useCaselawSeo({
        document: doc,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "Paragraph text here." }),
      );
    });

    it("falls back to default when document has no section paragraph", () => {
      const doc = new DOMParser().parseFromString(
        "<html lang='de'><body><div>No section paragraph</div></body></html>",
        "text/html",
      );
      useCaselawSeo({
        caseLaw: {} as CaseLaw,
        document: doc,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "Gerichtsentscheidung" }),
      );
    });
  });

  // TODO: Make sure the tests reflect the expected behavoir afer
  // the buildOgTitle function has been updated
  // oxlint-disable-next-line vitest/no-disabled-tests
  describe.skip("buildOgTitle", () => {
    it("builds ogTitle with court, documentType, date, and fileNumber", () => {
      useCaselawSeo({
        caseLaw: {
          courtName: "Bundesgerichtshof",
          documentType: "Urteil",
          decisionDate: "2023-06-15",
          fileNumbers: ["VIII ZR 12/23"],
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle: "Bundesgerichtshof: Urteil vom 15.06.2023 – VIII ZR 12/23",
        }),
      );
    });

    it("uses 'Gerichtsentscheidung' as documentType fallback", () => {
      useCaselawSeo({
        caseLaw: {
          courtName: "Bundesgerichtshof",
          decisionDate: "2023-06-15",
          fileNumbers: ["VIII ZR 12/23"],
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle:
            "Bundesgerichtshof: Gerichtsentscheidung vom 15.06.2023 – VIII ZR 12/23",
        }),
      );
    });

    it("omits court when courtName is missing", () => {
      useCaselawSeo({
        caseLaw: {
          documentType: "Urteil",
          decisionDate: "2023-06-15",
          fileNumbers: ["VIII ZR 12/23"],
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle: "Urteil vom 15.06.2023 – VIII ZR 12/23",
        }),
      );
    });

    it("truncates ogTitle at word boundary to 55 characters", () => {
      useCaselawSeo({
        caseLaw: {
          courtName: "Oberverwaltungsgericht Nordrhein-Westfalen Münster",
          documentType: "Beschluss",
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle: "Oberverwaltungsgericht Nordrhein-Westfalen Münster:",
        }),
      );
    });
  });
});
