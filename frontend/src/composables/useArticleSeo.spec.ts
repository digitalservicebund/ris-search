import { beforeEach, describe, expect, it, vi } from "vitest";
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { useArticleSeo } from "./useArticleSeo";
import type { Article } from "~/types/api";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useArticleSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("buildTitle", () => {
    it("uses abbreviation as base", () => {
      useArticleSeo({ abbreviation: "BGB" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "BGB" }),
      );
    });

    it("uses 'Gesetz' as fallback if abbreviation is missing", () => {
      useArticleSeo({});

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "Gesetz" }),
      );
    });

    it("appends paragraph number", () => {
      useArticleSeo({
        abbreviation: "BGB",
        article: { name: "§ 1" } as Article,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "BGB, § 1" }),
      );
    });

    it.each([
      ["2025-01-01/..", "BGB, vom 01.01.2025"],
      ["../2026-01-01", "BGB, bis 01.01.2026"],
      ["2025-01-01/2026-01-01", "BGB, 01.01.2025-01.01.2026"],
    ])(
      "appends formatted validity interval for temporalCoverage '%s'",
      (temporalCoverage, expected) => {
        useArticleSeo({
          abbreviation: "BGB",
          article: { temporalCoverage } as Article,
        });

        expect(useSeo).toHaveBeenCalledWith(
          expect.objectContaining({ title: expected }),
        );
      },
    );

    it("combines abbreviation, article name and validity interval", () => {
      useArticleSeo({
        abbreviation: "BGB",
        article: {
          name: "§ 1",
          temporalCoverage: "2025-01-01/2026-01-01",
        } as Article,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "BGB, § 1, 01.01.2025-01.01.2026" }),
      );
    });
  });

  describe("buildDescription", () => {
    it("returns undefined if articleHtml is absent", () => {
      useArticleSeo({});

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: undefined }),
      );
    });

    it("returns undefined if articleHtml has no paragraph", () => {
      useArticleSeo({ articleHtml: "<div>no paragraph here</div>" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: undefined }),
      );
    });

    it("returns undefined if the first paragraph is empty", () => {
      useArticleSeo({ articleHtml: "<p>   </p>" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: undefined }),
      );
    });

    it("extracts text from the first paragraph", () => {
      useArticleSeo({ articleHtml: "<p>Hello world</p><p>Second</p>" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "Hello world" }),
      );
    });

    it("strips HTML tags from the paragraph", () => {
      useArticleSeo({ articleHtml: "<p>Hello <b>world</b></p>" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "Hello world" }),
      );
    });

    it("truncates description at 150 characters word boundary", () => {
      const longText =
        "This is a long text " +
        "LongWordWithoutWhiteSpacesShouldGetTruncated".repeat(3).trim();
      useArticleSeo({ articleHtml: `<p>${longText}</p>` });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          description: "This is a long text",
        }),
      );
    });
  });

  describe("buildOgTitle", () => {
    it("returns undefined if neither abbreviation nor headline is provided", () => {
      useArticleSeo({});

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: undefined }),
      );
    });

    it("uses abbreviation when no headline is provided", () => {
      useArticleSeo({ abbreviation: "BGB" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "BGB" }),
      );
    });

    it("uses headline when no abbreviation is provided", () => {
      useArticleSeo({ articleHeadlineHtml: "<p>§ 1 Allgemeines</p>" });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "§ 1 Allgemeines" }),
      );
    });

    it("combines abbreviation and headline with a colon separator", () => {
      useArticleSeo({
        abbreviation: "BGB",
        articleHeadlineHtml: "<p>§ 1 Allgemeines</p>",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "BGB: § 1 Allgemeines" }),
      );
    });

    it("strips HTML tags from the headline", () => {
      useArticleSeo({
        abbreviation: "BGB",
        articleHeadlineHtml: "<p>§ 1 <b>Allgemeines</b></p>",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "BGB: § 1 Allgemeines" }),
      );
    });

    it("normalizes whitespace in the headline", () => {
      useArticleSeo({
        articleHeadlineHtml: "<p>§ 1      Allgemeines</p>",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "§ 1 Allgemeines" }),
      );
    });

    it("truncates ogTitle at 55 characters word boundary", () => {
      const longHeadline =
        "A very long paragraph thisReallyLongWordWillGetTruncated";
      useArticleSeo({
        abbreviation: "BGB",
        articleHeadlineHtml: `<p>${longHeadline}</p>`,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle: "BGB: A very long paragraph",
        }),
      );
    });
  });
});
