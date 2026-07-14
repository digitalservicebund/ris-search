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
    it("uses the headline", () => {
      useCaselawSeo({
        caseLaw: {
          headline: "Caselaw short title",
        } as CaseLaw,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Caselaw short title",
        }),
      );
    });

    it("falls back to 'Gerichtsentscheidung' without a headline", () => {
      useCaselawSeo({ caseLaw: {} as CaseLaw });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "Gerichtsentscheidung",
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

    it("truncates description at 150 characters word boundary", () => {
      const guidingPrinciple =
        "This is a long text " +
        "LongWordWithoutWhiteSpacesShouldGetTruncated".repeat(3).trim();
      useCaselawSeo({ caseLaw: { guidingPrinciple } as CaseLaw });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          description: "This is a long text",
        }),
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
