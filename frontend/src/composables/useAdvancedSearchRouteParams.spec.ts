import { reactive, nextTick } from "vue";
import { DocumentKind } from "~/types";

describe("useAdvancedSearchRouteParams", () => {
  beforeEach(() => {
    vi.resetModules();
  });

  describe("document kind", () => {
    it("restores a valid document kind from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { documentKind: "R" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { documentKind } = useAdvancedSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.CaseLaw);
    });

    it("returns 'Norm' when the document kind is invalid", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { documentKind: "invalid" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { documentKind } = useAdvancedSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.Norm);
    });

    it("returns 'Norm' when the query does not contain a document kind", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { documentKind } = useAdvancedSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.Norm);
    });
  });

  describe("query", () => {
    it("restores a search query from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { q: "example" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { query } = useAdvancedSearchRouteParams();

      expect(query.value).toBe("example");
    });

    it("returns an empty string search query by default", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { query } = useAdvancedSearchRouteParams();

      expect(query.value).toBe("");
    });

    it("decodes a URI encoded query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { q: encodeURIComponent("DATUM:>2021-01-01") },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { query } = useAdvancedSearchRouteParams();

      expect(query.value).toBe("DATUM:>2021-01-01");
    });
  });

  describe("date filter", () => {
    it("restores a valid date filter type from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { dateFilterType: "allTime" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.type).toBe("allTime");
    });

    it("returns the fallback if the filter type is not valid for the document kind", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { documentKind: "R", dateFilterType: "currentlyInForce" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.type).toBe("allTime");
    });

    it("returns the default filter type for 'Norm' documents", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { documentKind: "N" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.type).toBe("currentlyInForce");
    });

    it("returns the default filter type for other documents", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { documentKind: "R" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.type).toBe("allTime");
    });

    it("returns the 'from' date of the date filter", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { dateFilterFrom: "2023-01-01" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.from).toBe("2023-01-01");
    });

    it("returns an empty string if the 'from' date is not in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.from).toBeUndefined();
    });

    it("returns the 'to' date of the date filter", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { dateFilterTo: "2023-12-31" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.to).toBe("2023-12-31");
    });

    it("returns an empty string if the 'to' date is not in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter } = useAdvancedSearchRouteParams();

      expect(dateFilter.value.to).toBeUndefined();
    });
  });

  describe("sort", () => {
    it("returns the sort param from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { sort: "relevance" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { sort } = useAdvancedSearchRouteParams();

      expect(sort.value).toBe("relevance");
    });

    it("returns the default sort param from the query as a fallback", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { sort } = useAdvancedSearchRouteParams();

      expect(sort.value).toBe("default");
    });
  });

  describe("items per page", () => {
    it("returns the number of items per page from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { itemsPerPage: "100" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { itemsPerPage } = useAdvancedSearchRouteParams();

      expect(itemsPerPage.value).toBe("100");
    });

    it("returns the default number of items per page from the query as a fallback", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { itemsPerPage } = useAdvancedSearchRouteParams();

      expect(itemsPerPage.value).toBe("50");
    });
  });

  describe("page index", () => {
    it("returns the page index from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { pageIndex: "5" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { pageIndex } = useAdvancedSearchRouteParams();

      expect(pageIndex.value).toBe(5);
    });

    it("returns the first page if the value from the query can't be parsed", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { pageIndex: "invalid" },
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { pageIndex } = useAdvancedSearchRouteParams();

      expect(pageIndex.value).toBe(0);
    });

    it("returns the first page if there is no value in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { pageIndex } = useAdvancedSearchRouteParams();

      expect(pageIndex.value).toBe(0);
    });
  });

  describe("saveFilterStateToRoute", () => {
    it("calls navigateTo with all filter parameters", async () => {
      const navigateToMock = vi.fn();
      const existingQuery = { existingParam: "value" };

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: existingQuery,
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const {
        query,
        documentKind,
        dateFilter,
        pageIndex,
        sort,
        itemsPerPage,
        saveFilterStateToRoute,
      } = useAdvancedSearchRouteParams();

      query.value = "test search";
      documentKind.value = DocumentKind.CaseLaw;
      dateFilter.value = {
        type: "allTime",
        from: "2023-01-01",
        to: "2023-12-31",
      };
      pageIndex.value = 2;
      sort.value = "relevance";
      itemsPerPage.value = "100";

      await saveFilterStateToRoute();

      expect(navigateToMock).toHaveBeenCalledWith({
        query: {
          existingParam: "value",
          q: "test%20search",
          documentKind: "R",
          dateFilterType: "allTime",
          dateFilterFrom: "2023-01-01",
          dateFilterTo: "2023-12-31",
          pageIndex: 2,
          sort: "relevance",
          itemsPerPage: "100",
        },
      });
    });

    it("encodes the query string", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { query, saveFilterStateToRoute } = useAdvancedSearchRouteParams();

      query.value = "DATUM:>2021-01-01";
      await saveFilterStateToRoute();

      expect(navigateToMock).toHaveBeenCalledWith({
        query: expect.objectContaining({
          q: encodeURIComponent("DATUM:>2021-01-01"),
        }),
      });
    });

    it("handles undefined date filter values by setting empty strings", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { dateFilter, saveFilterStateToRoute } =
        useAdvancedSearchRouteParams();

      dateFilter.value = {
        type: "allTime",
        from: undefined,
        to: undefined,
      };

      await saveFilterStateToRoute();

      expect(navigateToMock).toHaveBeenCalledWith({
        query: expect.objectContaining({
          dateFilterFrom: "",
          dateFilterTo: "",
        }),
      });
    });
  });

  it("reacts to route changes", async () => {
    const routeQuery = reactive({ query: { q: "test before" } });

    vi.doMock("#app", () => ({
      useRoute: vi.fn().mockReturnValue(routeQuery),
    }));

    const { useAdvancedSearchRouteParams } =
      await import("./useAdvancedSearchRouteParams");

    const { query } = useAdvancedSearchRouteParams();

    expect(query.value).toEqual("test before");

    routeQuery.query = { q: "test after" };
    await nextTick();

    expect(query.value).toEqual("test after");
  });
});
