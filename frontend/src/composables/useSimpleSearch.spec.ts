import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref, type ComputedRef, type Ref } from "vue";
import { useSimpleSearch } from "./useSimpleSearch";
import { DocumentKind } from "~/types";

const { useRisBackendMock, executeMock, getCurrentDateMock } = vi.hoisted(
  () => {
    const executeMock = vi.fn();

    return {
      useRisBackendMock: vi.fn<
        (
          url: Ref<string>,
          opts: { query: ComputedRef<SimpleSearchEndpointParams> },
        ) => unknown
      >(() => ({
        status: ref("success"),
        data: computed(() => ref({ content: [], totalItems: 0 })),
        error: ref(null),
        pending: ref(false),
        execute: executeMock,
        refresh: vi.fn(),
        clear: vi.fn(),
      })),
      executeMock,
      getCurrentDateMock: vi.fn(() => "2024-01-15"),
    };
  },
);

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

vi.mock("~/utils/dateFormatting", () => ({
  getCurrentDateInGermanyFormatted: getCurrentDateMock,
}));

/** Helper to get the query parameter value from the mock call */
function getQueryValue(): SimpleSearchEndpointParams {
  return useRisBackendMock.mock.calls[0]![1].query.value;
}

describe("useSimpleSearch", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("endpoint URLs", () => {
    it("calls the endpoint for case law with correct URL", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      const url = useRisBackendMock.mock.calls[0]![0];
      expect(url.value).toBe("/v1/case-law");
    });

    it("calls the endpoint for legislation with correct URL", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.Norm },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      const url = useRisBackendMock.mock.calls[0]![0];
      expect(url.value).toBe("/v1/legislation");
    });

    it("calls the endpoint for literature with correct URL", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.Literature },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      const url = useRisBackendMock.mock.calls[0]![0];
      expect(url.value).toBe("/v1/literature");
    });

    it("calls the endpoint for administrative directive with correct URL", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.AdministrativeDirective },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      const url = useRisBackendMock.mock.calls[0]![0];
      expect(url.value).toBe("/v1/administrative-directive");
    });

    it("defaults to document search for DocumentKind.All", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.All },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      const url = useRisBackendMock.mock.calls[0]![0];
      expect(url.value).toBe("/v1/document");
    });
  });

  describe("query parameters", () => {
    it("submits the search term", async () => {
      await useSimpleSearch(
        "test query",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({ searchTerm: "test query" });
    });

    it("submits pagination parameters correctly", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        { itemsPerPage: "25", pageIndex: 2 },
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({ size: "25", pageIndex: 2 });
    });

    it("submits sort order parameter correctly", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        { sort: "date" },
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({ sort: "date" });
    });

    it("uses default values for pagination and sort", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({
        size: "10",
        pageIndex: 0,
        sort: "default",
      });
    });
  });

  describe("date filter", () => {
    it("includes date filter when provided with date range", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        { type: "period", from: "2023-01-01", to: "2023-12-31" },
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({
        dateFrom: "2023-01-01",
        dateTo: "2023-12-31",
      });
    });

    it("does not include date filter when type is allTime", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        { type: "allTime", from: undefined, to: undefined },
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue().dateFrom).toBeUndefined();
      expect(getQueryValue().dateTo).toBeUndefined();
    });

    it("does not include date filter when undefined", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue().dateFrom).toBeUndefined();
      expect(getQueryValue().dateTo).toBeUndefined();
    });
  });

  describe("case-law specific parameters", () => {
    it("includes typeGroup when provided and not 'all'", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw, typeGroup: "test" },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({ typeGroup: "test" });
    });

    it("does not include typeGroup when set to 'all'", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw, typeGroup: "all" },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue().typeGroup).toBeUndefined();
    });

    it("does not include typeGroup for non-caselaw document kinds", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.Norm, typeGroup: "test" },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue().typeGroup).toBeUndefined();
    });

    it("includes court when provided for case law", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        "BGH",
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({ court: "BGH" });
    });

    it("does not include court for non-caselaw document kinds", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.Norm },
        undefined,
        "BGH",
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue().court).toBeUndefined();
    });
  });

  describe("norm-specific parameters", () => {
    it("includes mostRelevantOn for norm searches", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.Norm },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({ mostRelevantOn: "2024-01-15" });
    });

    it("includes mostRelevantOn for DocumentKind.All searches", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.All },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue()).toMatchObject({ mostRelevantOn: "2024-01-15" });
    });

    it("does not include mostRelevantOn for case law searches", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      expect(useRisBackendMock).toHaveBeenCalled();
      expect(getQueryValue().mostRelevantOn).toBeUndefined();
    });
  });

  describe("return values", () => {
    it("submitSearch executes the query", async () => {
      const { submitSearch } = await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      await submitSearch();

      expect(executeMock).toHaveBeenCalled();
    });

    it("totalItemCount returns 0 when no data", async () => {
      const { totalItemCount } = await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      expect(totalItemCount.value).toBe(0);
    });
  });
});
