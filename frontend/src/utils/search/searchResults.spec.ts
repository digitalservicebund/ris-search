import { describe, expect, it } from "vitest";
import type { TextMatch } from "~/types/api";
import {
  TITLE_FALLBACK,
  getMatch,
  getMatches,
  getTitleWithFallback,
} from "./searchResults";

const matches: TextMatch[] = [
  { name: "headline", text: "The <mark>Main</mark> Title" },
  { name: "headline", text: "Second headline" },
  { name: "grounds", text: "<b>Bold</b> grounds text" },
  { name: "fileNumbers", text: "AZ <mark>123</mark>" },
  { name: "fileNumbers", text: "AZ <mark>456</mark>" },
];

describe("searchResults", () => {
  describe("getMatch", () => {
    it("returns the sanitized text of the first match for a given name", () => {
      expect(getMatch("headline", matches)).toBe("The <mark>Main</mark> Title");
    });

    it("sanitizes disallowed tags from the matched text", () => {
      const m: TextMatch[] = [
        { name: "field", text: "<div><mark>hit</mark></div>" },
      ];
      expect(getMatch("field", m)).toBe("<mark>hit</mark>");
    });

    it("returns undefined when no match is found", () => {
      expect(getMatch("missing", matches)).toBeUndefined();
    });

    it("returns undefined for an empty matches array", () => {
      expect(getMatch("headline", [])).toBeUndefined();
    });
  });

  describe("getMatches", () => {
    it("returns all sanitized texts for a given name", () => {
      expect(getMatches("fileNumbers", matches)).toEqual([
        "AZ <mark>123</mark>",
        "AZ <mark>456</mark>",
      ]);
    });

    it("returns only the sanitized texts for the requested name", () => {
      expect(getMatches("headline", matches)).toEqual([
        "The <mark>Main</mark> Title",
        "Second headline",
      ]);
    });

    it("sanitizes disallowed tags from each match", () => {
      const m: TextMatch[] = [
        { name: "field", text: "<img src='x'/><mark>hit</mark>" },
        { name: "field", text: "<script>alert(1)</script>clean" },
      ];
      expect(getMatches("field", m)).toEqual(["<mark>hit</mark>", "clean"]);
    });

    it("returns an empty array when no matches are found", () => {
      expect(getMatches("missing", matches)).toEqual([]);
    });

    it("returns an empty array for an empty matches array", () => {
      expect(getMatches("headline", [])).toEqual([]);
    });
  });

  describe("getTitleWithFallback", () => {
    it("returns the first truthy candidate", () => {
      expect(getTitleWithFallback("Plain title", "Other")).toBe("Plain title");
    });

    it("skips falsy candidates and returns the first truthy one", () => {
      expect(getTitleWithFallback(undefined, null, "", "Fallback title")).toBe(
        "Fallback title",
      );
    });

    it("sanitizes disallowed tags from the resolved value", () => {
      expect(
        getTitleWithFallback("<div><mark>Highlighted</mark> title</div>"),
      ).toBe("<mark>Highlighted</mark> title");
    });

    it("returns fallback when all candidates are falsy", () => {
      expect(getTitleWithFallback(undefined, null, "")).toBe(TITLE_FALLBACK);
    });

    it("returns fallback when called with no arguments", () => {
      expect(getTitleWithFallback()).toBe(TITLE_FALLBACK);
    });
  });
});
