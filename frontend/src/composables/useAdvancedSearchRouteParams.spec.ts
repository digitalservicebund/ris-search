import { reactive, nextTick } from "vue";
import { DocumentKind } from "~/types/api";

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

    it("returns undefined if the 'from' date is not in the query", async () => {
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

    it("returns undefined if the 'to' date is not in the query", async () => {
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

      expect(itemsPerPage.value).toBe("25");
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

  describe("navigateToSearch", () => {
    it("retains the hash when the query did not change", async () => {
      const navigateToMock = vi.fn();

      const query = {
        q: "test%20search",
        documentKind: "N",
        dateFilterType: "currentlyInForce",
        dateFilterFrom: "",
        dateFilterTo: "",
        pageIndex: "0",
        sort: "default",
        itemsPerPage: "50",
      };

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({ query, hash: "#some-anchor" }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({});

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({ hash: "#some-anchor" }),
        expect.anything(),
      );
    });

    it("retains the hash when the query was empty", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({ query: {}, hash: "#some-anchor" }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({});

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({ hash: "#some-anchor" }),
        expect.anything(),
      );
    });

    it("resets the hash when the query changes", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { q: "old%20search" },
          hash: "#some-anchor",
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({ query: "new search" });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({ hash: undefined }),
        expect.anything(),
      );
    });

    it("calls navigation with all filter parameters", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
          hash: "",
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({
        query: "test search",
        documentKind: DocumentKind.CaseLaw,
        dateFilter: {
          type: "allTime",
          from: "2023-01-01",
          to: "2023-12-31",
        },
        pageIndex: 2,
        sort: "relevance",
        itemsPerPage: "100",
      });

      expect(navigateToMock).toHaveBeenCalledWith(
        {
          hash: "",
          query: {
            q: "test%20search",
            documentKind: "R",
            dateFilterType: "allTime",
            dateFilterFrom: "2023-01-01",
            dateFilterTo: "2023-12-31",
            pageIndex: "2",
            sort: "relevance",
            itemsPerPage: "100",
          },
        },
        { replace: undefined },
      );
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

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({ query: "DATUM:>2021-01-01" });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            q: encodeURIComponent("DATUM:>2021-01-01"),
          }),
        }),
        expect.anything(),
      );
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

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({
        dateFilter: { type: "allTime", from: undefined, to: undefined },
      });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            dateFilterFrom: "",
            dateFilterTo: "",
          }),
        }),
        expect.anything(),
      );
    });

    it("passes replace option to navigation", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({ pageIndex: 3 }, { replace: true });

      expect(navigateToMock).toHaveBeenCalledWith(expect.anything(), {
        replace: true,
      });
    });

    it("resets query and date filter when document kind changes", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {
            documentKind: "N",
            q: "some%20query",
            dateFilterType: "currentlyInForce",
          },
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({ documentKind: DocumentKind.CaseLaw });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            q: "",
            documentKind: "R",
            dateFilterType: "allTime",
          }),
        }),
        expect.anything(),
      );
    });

    it("filters by currently in force when switching to Norm", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {
            documentKind: "R",
            dateFilterType: "allTime",
          },
        }),
        navigateTo: navigateToMock,
      }));

      const { useAdvancedSearchRouteParams } =
        await import("./useAdvancedSearchRouteParams");

      const { navigateToSearch } = useAdvancedSearchRouteParams();

      await navigateToSearch({ documentKind: DocumentKind.Norm });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            documentKind: "N",
            dateFilterType: "currentlyInForce",
          }),
        }),
        expect.anything(),
      );
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
