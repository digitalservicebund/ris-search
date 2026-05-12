import { describe, it, expect } from "vitest";
import { DocumentKind } from "~/types/api";
import { buildSearchTitle } from "./useSimpleSearchSeo";

describe("buildSearchTitle", () => {
  describe("no query, no pagination", () => {
    it("uses 'Suche' for an all document kind search", () => {
      expect(buildSearchTitle(undefined, DocumentKind.All, 0)).toBe("Suche");
    });

    it("uses the document kind label on document specific searches", () => {
      expect(buildSearchTitle(undefined, DocumentKind.CaseLaw, 0)).toBe(
        "Gerichtsentscheidungen, Suche",
      );
      expect(buildSearchTitle(undefined, DocumentKind.Norm, 0)).toBe(
        "Gesetze & Verordnungen, Suche",
      );
      expect(buildSearchTitle(undefined, DocumentKind.Literature, 0)).toBe(
        "Literaturnachweise, Suche",
      );
      expect(
        buildSearchTitle(undefined, DocumentKind.AdministrativeDirective, 0),
      ).toBe("Verwaltungsvorschriften, Suche");
    });
  });

  describe("with query", () => {
    it("includes the query term", () => {
      expect(buildSearchTitle("Mietrecht", DocumentKind.All, 0)).toBe(
        "Suche, Mietrecht",
      );
    });

    it("query takes precedence over document kind", () => {
      expect(buildSearchTitle("BGB", DocumentKind.Norm, 0)).toBe("Suche, BGB");
    });
  });

  describe("pagination", () => {
    it("appends page suffix starting from page 2", () => {
      expect(buildSearchTitle(undefined, DocumentKind.All, 1)).toBe(
        "Suche, Seite 2",
      );
      expect(buildSearchTitle(undefined, DocumentKind.All, 4)).toBe(
        "Suche, Seite 5",
      );
    });

    it("combines query and page suffix for all document kind searches", () => {
      expect(buildSearchTitle("BGB", DocumentKind.All, 1)).toBe(
        "Suche, BGB, Seite 2",
      );
    });

    it("combines document kind and page suffix", () => {
      expect(buildSearchTitle(undefined, DocumentKind.CaseLaw, 2)).toBe(
        "Gerichtsentscheidungen, Suche, Seite 3",
      );
    });
  });
});
