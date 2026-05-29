import { reactive, nextTick } from "vue";
import { DocumentKind } from "~/types/api";

describe("useSimpleSearchRouteParams", () => {
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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { documentKind } = useSimpleSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.CaseLaw);
    });

    it("returns 'All' when the document kind is invalid", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { documentKind: "invalid" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { documentKind } = useSimpleSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.All);
    });

    it("returns 'All' when the query does not contain a document kind", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { documentKind } = useSimpleSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.All);
    });
  });

  describe("query", () => {
    it("restores a search query from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { query: "example" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { query } = useSimpleSearchRouteParams();

      expect(query.value).toBe("example");
    });

    it("returns an empty string search query by default", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { query } = useSimpleSearchRouteParams();

      expect(query.value).toBe("");
    });

    it("decodes a URI encoded query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { query: encodeURIComponent("DATUM:>2021-01-01") },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { query } = useSimpleSearchRouteParams();

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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter } = useSimpleSearchRouteParams();

      expect(dateFilter.value.type).toBe("allTime");
    });

    it("returns 'allTime' when the date filter type is invalid", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { dateFilterType: "invalid" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter } = useSimpleSearchRouteParams();

      expect(dateFilter.value.type).toBe("allTime");
    });

    it("returns the default filter type when there is no value in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter } = useSimpleSearchRouteParams();

      expect(dateFilter.value.type).toBe("allTime");
    });

    it("returns the 'from' date of the date filter", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { dateFilterFrom: "2023-01-01" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter } = useSimpleSearchRouteParams();

      expect(dateFilter.value.from).toBe("2023-01-01");
    });

    it("returns undefined if the 'from' date is not in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter } = useSimpleSearchRouteParams();

      expect(dateFilter.value.from).toBeUndefined();
    });

    it("returns the 'to' date of the date filter", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { dateFilterTo: "2023-12-31" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter } = useSimpleSearchRouteParams();

      expect(dateFilter.value.to).toBe("2023-12-31");
    });

    it("returns undefined if the 'to' date is not in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter } = useSimpleSearchRouteParams();

      expect(dateFilter.value.to).toBeUndefined();
    });
  });

  describe("court", () => {
    it("restores the court from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { court: "BGH" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { court } = useSimpleSearchRouteParams();

      expect(court.value).toBe("BGH");
    });

    it("returns undefined when the court is not in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { court } = useSimpleSearchRouteParams();

      expect(court.value).toBeUndefined();
    });
  });

  describe("type group", () => {
    it("restores the type group from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { typeGroup: "example-type-group" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { typeGroup } = useSimpleSearchRouteParams();

      expect(typeGroup.value).toBe("example-type-group");
    });

    it("returns undefined when the type group is not in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { typeGroup } = useSimpleSearchRouteParams();

      expect(typeGroup.value).toBeUndefined();
    });
  });

  describe("sort", () => {
    it("returns the sort param from the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { sort: "relevance" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { sort } = useSimpleSearchRouteParams();

      expect(sort.value).toBe("relevance");
    });

    it("returns the default sort param as a fallback", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { sort } = useSimpleSearchRouteParams();

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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { itemsPerPage } = useSimpleSearchRouteParams();

      expect(itemsPerPage.value).toBe("100");
    });

    it("returns the default number of items per page as a fallback", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { itemsPerPage } = useSimpleSearchRouteParams();

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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { pageIndex } = useSimpleSearchRouteParams();

      expect(pageIndex.value).toBe(5);
    });

    it("returns the first page if the value from the query can't be parsed", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: { pageIndex: "invalid" },
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { pageIndex } = useSimpleSearchRouteParams();

      expect(pageIndex.value).toBe(0);
    });

    it("returns the first page if there is no value in the query", async () => {
      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {},
        }),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { pageIndex } = useSimpleSearchRouteParams();

      expect(pageIndex.value).toBe(0);
    });
  });

  describe("navigateToSearch", () => {
    it("retains the hash when the query did not change", async () => {
      const navigateToMock = vi.fn();

      const query = {
        query: "test%20search",
        documentKind: "R",
        court: "BGH",
        typeGroup: "",
        dateFilterType: "allTime",
        dateFilterFrom: "",
        dateFilterTo: "",
        pageIndex: "0",
        sort: "default",
        itemsPerPage: "10",
      };

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({ query, hash: "#some-anchor" }),
        navigateTo: navigateToMock,
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

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
          query: { query: "old%20search" },
          hash: "#some-anchor",
        }),
        navigateTo: navigateToMock,
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

      await navigateToSearch({
        query: "test search",
        documentKind: DocumentKind.CaseLaw,
        court: "BGH",
        typeGroup: "example-type-group",
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
            query: "test%20search",
            court: "BGH",
            documentKind: "R",
            typeGroup: "example-type-group",
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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

      await navigateToSearch({ query: "DATUM:>2021-01-01" });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            query: encodeURIComponent("DATUM:>2021-01-01"),
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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

      await navigateToSearch({ pageIndex: 3 }, { replace: true });

      expect(navigateToMock).toHaveBeenCalledWith(expect.anything(), {
        replace: true,
      });
    });

    it("resets date filter when document kind changes", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {
            documentKind: "R",
            dateFilterType: "period",
            dateFilterFrom: "2024-01-01",
            dateFilterTo: "2024-12-31",
          },
        }),
        navigateTo: navigateToMock,
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

      await navigateToSearch({ documentKind: DocumentKind.Norm });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            dateFilterType: "allTime",
            dateFilterFrom: "",
            dateFilterTo: "",
          }),
        }),
        expect.anything(),
      );
    });

    it("resets type group and court when changing from Case Law", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {
            documentKind: "R",
            typeGroup: "urteil",
            court: "BGH",
          },
        }),
        navigateTo: navigateToMock,
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

      await navigateToSearch({ documentKind: DocumentKind.All });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            typeGroup: "",
            court: "",
          }),
        }),
        expect.anything(),
      );
    });

    it("does not reset type group and court when staying on Case Law", async () => {
      const navigateToMock = vi.fn();

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue({
          query: {
            documentKind: "R",
            typeGroup: "urteil",
            court: "BGH",
          },
        }),
        navigateTo: navigateToMock,
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { navigateToSearch } = useSimpleSearchRouteParams();

      await navigateToSearch({ query: "test" });

      expect(navigateToMock).toHaveBeenCalledWith(
        expect.objectContaining({
          query: expect.objectContaining({
            typeGroup: "urteil",
            court: "BGH",
          }),
        }),
        expect.anything(),
      );
    });
  });

  it("reacts to route changes", async () => {
    const routeQuery = reactive({ query: { query: "test before" } });

    vi.doMock("#app", () => ({
      useRoute: vi.fn().mockReturnValue(routeQuery),
    }));

    const { useSimpleSearchRouteParams } =
      await import("./useSimpleSearchRouteParams");

    const { query } = useSimpleSearchRouteParams();

    expect(query.value).toEqual("test before");

    routeQuery.query = { query: "test after" };
    await nextTick();

    expect(query.value).toEqual("test after");
  });
});
