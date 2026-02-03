import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref, type ComputedRef, type Ref } from "vue";
import {
  useSimpleSearch,
  type SimpleSearchEndpointParams,
} from "./useSimpleSearch";
import { DocumentKind } from "~/types";

type BackendOptions = {
  query: ComputedRef<SimpleSearchEndpointParams>;
  onRequest?: (ctx: { options: { query: unknown } }) => void;
  onResponse?: (ctx: { response: { _data?: { totalItems?: number } } }) => void;
};

const mockPostHog = {
  searchPerformed: vi.fn(),
  noSearchResults: vi.fn(),
};

const {
  useRisBackendMock,
  executeMock,
  getCurrentDateMock,
  usePostHogMock,
  capturedOptions,
} = vi.hoisted(() => {
  const executeMock = vi.fn();
  const capturedOptions: { current?: BackendOptions } = {};

  return {
    useRisBackendMock: vi.fn<
      (url: Ref<string>, opts: BackendOptions) => unknown
    >((_, opts) => {
      capturedOptions.current = opts;
      return {
        status: ref("success"),
        data: computed(() => ref({ content: [], totalItems: 0 })),
        error: ref(null),
        pending: ref(false),
        execute: executeMock,
        refresh: vi.fn(),
        clear: vi.fn(),
      };
    }),
    executeMock,
    getCurrentDateMock: vi.fn(() => "2024-01-15"),
    usePostHogMock: () => mockPostHog,
    capturedOptions: capturedOptions,
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

vi.mock("~/utils/dateFormatting", () => ({
  getCurrentDateInGermanyFormatted: getCurrentDateMock,
}));

vi.mock("~/composables/usePostHog", () => ({
  usePostHog: usePostHogMock,
}));

/** Helper to get the query parameter value from the mock call */
function getQueryValue(): SimpleSearchEndpointParams {
  return useRisBackendMock.mock.calls[0]![1].query.value;
}

/** Helper to simulate the onRequest callback being triggered */
function simulateOnRequest(query: unknown) {
  capturedOptions.current?.onRequest?.({ options: { query } });
}

/** Helper to simulate the onResponse callback being triggered */
function simulateOnResponse(totalItems?: number) {
  capturedOptions.current?.onResponse?.({
    response: { _data: { totalItems } },
  });
}

describe("useSimpleSearch", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockPostHog.searchPerformed.mockClear();
    mockPostHog.noSearchResults.mockClear();
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

  describe("PostHog integration", () => {
    it("calls searchPerformed on request with undefined previous query for initial search", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      const query = getQueryValue();
      simulateOnRequest(query);

      expect(mockPostHog.searchPerformed).toHaveBeenCalledWith(
        "simple",
        query,
        undefined,
      );
    });

    it("calls searchPerformed with previous query when query changes", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      const firstQuery = { ...getQueryValue() };

      // Simulate first request
      simulateOnRequest(firstQuery);
      mockPostHog.searchPerformed.mockClear();

      // Simulate second request with different query
      const secondQuery = { ...firstQuery, searchTerm: "different" };
      simulateOnRequest(secondQuery);

      expect(mockPostHog.searchPerformed).toHaveBeenCalledWith(
        "simple",
        secondQuery,
        firstQuery,
      );
    });

    it("calls searchPerformed with undefined previous query when same query is submitted twice", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      const query = { ...getQueryValue() };

      // Simulate first request
      simulateOnRequest(query);
      mockPostHog.searchPerformed.mockClear();

      // Simulate second request with identical query
      simulateOnRequest(query);

      expect(mockPostHog.searchPerformed).toHaveBeenCalledWith(
        "simple",
        query,
        undefined,
      );
    });

    it("calls noSearchResults when response has no totalItems", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      simulateOnResponse(0);

      expect(mockPostHog.noSearchResults).toHaveBeenCalled();
    });

    it("calls noSearchResults when response has undefined totalItems", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      simulateOnResponse();

      expect(mockPostHog.noSearchResults).toHaveBeenCalled();
    });

    it("does not call noSearchResults when response has results", async () => {
      await useSimpleSearch(
        "example",
        { documentKind: DocumentKind.CaseLaw },
        undefined,
        undefined,
        {},
      );

      simulateOnResponse(10);

      expect(mockPostHog.noSearchResults).not.toHaveBeenCalled();
    });
  });
});
