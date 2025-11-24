import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, expect, it, vi } from "vitest";
import { categoryToDocumentKind, convertParams, getUrl } from "./simpleSearch";
import { DocumentKind } from "~/types";

describe("getUrl", () => {
  it("returns the correct URL for known catrgories", async () => {
    expect(getUrl("R")).toBe("/v1/case-law");
    expect(getUrl("N")).toBe("/v1/legislation");
    expect(getUrl("L")).toBe("/v1/literature");
  });

  it("returns /v1/document for unknown categories", async () => {
    expect(getUrl("foo")).toBe("/v1/document");
  });
});

describe("categoryToDocumentKind", () => {
  it.each([
    ["A", DocumentKind.All],
    ["N", DocumentKind.Norm],
    ["R", DocumentKind.CaseLaw],
    ["R.all", DocumentKind.CaseLaw],
    ["R.urteil", DocumentKind.CaseLaw],
    ["R.beschluss", DocumentKind.CaseLaw],
    ["R.other", DocumentKind.CaseLaw],
    ["R", DocumentKind.CaseLaw],
    ["L", DocumentKind.Literature],
  ])(
    "category '%s' is converted to documenttype '%s'",
    (category, documentKind) => {
      expect(categoryToDocumentKind(category)).toBe(documentKind);
    },
  );
});

const testParams = {
  query: "test",
  category: "A",
  itemsPerPage: 10,
  pageNumber: 1,
  sort: "date",
  date: "2023-01-01",
};

mockNuxtImport("getCurrentDateInGermanyFormatted", () => {
  return vi.fn(() => {
    return "2025-01-01";
  });
});

describe("convertParams", () => {
  it.each([
    "A",
    "N",
    "R",
    "R.all",
    "R.urteil",
    "R.beschluss",
    "R.other",
    "R",
    "L",
  ])("adds all common parameters when category is '%s'", (category) => {
    const params = {
      ...testParams,
      category: category,
    };

    const result = convertParams(params);
    expect(result).toEqual(
      expect.objectContaining({
        searchTerm: "test",
        size: "10",
        pageIndex: "1",
        sort: "date",
        dateFrom: "2023-01-01",
        dateTo: "2023-01-01",
      }),
    );
  });

  it.each(["N", "L"])(
    "does not add case-law specific params to searches with category '%s'",
    (category) => {
      const params = {
        ...testParams,
        court: "FooBar", // This should not be added as param
        category: category,
      };

      const result = convertParams(params);
      expect(result.typeGroup).toBeUndefined();
      expect(result.court).toBeUndefined();
    },
  );

  it.each(["A", "R", "R.all", "R.urteil", "R.beschluss", "R.other"])(
    "adds court only if it exists and search has category '%s'",
    (category) => {
      const paramsWithCourt = {
        ...testParams,
        court: "FooBar",
        category: category,
      };

      const resultWithCourts = convertParams(paramsWithCourt);
      expect(resultWithCourts.court).toBe("FooBar");

      const paramsWithoutCourt = {
        ...testParams,
        category: category,
      };

      const resultWithoutCourts = convertParams(paramsWithoutCourt);
      expect(resultWithoutCourts.court).toBeUndefined();
    },
  );

  it.each([
    ["all", "R.all"],
    ["urteil", "R.urteil"],
    ["beschluss", "R.beschluss"],
    ["other", "R.other"],
  ])(
    "sets typeGroup to '%s' for searches with category '%s'",
    (typeGroup, category) => {
      const params = {
        ...testParams,
        category: category,
      };

      const result = convertParams(params);
      expect(result.typeGroup).toBe(typeGroup);
    },
  );

  it.each(["R", "R.all", "R.urteil", "R.beschluss", "R.other", "R", "L"])(
    "does not add norms sepcific params to searches with category '%s'",
    (category) => {
      const params = {
        ...testParams,
        category: category,
      };

      const result = convertParams(params);
      expect(result.mostRelevantOn).toBeUndefined();
    },
  );

  it.each(["A", "N"])(
    "adds mostRelevantOn parameter to searches with category '%s'",
    (category) => {
      const params = {
        ...testParams,
        category: category,
      };

      const result = convertParams(params);
      expect(result.mostRelevantOn).toBe("2025-01-01");
    },
  );
});
