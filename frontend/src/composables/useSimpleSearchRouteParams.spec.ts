import { reactive, nextTick } from "vue";
import { DocumentKind } from "~/types";

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

      expect(itemsPerPage.value).toBe("10");
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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const {
        query,
        documentKind,
        court,
        typeGroup,
        dateFilter,
        pageIndex,
        sort,
        itemsPerPage,
        saveFilterStateToRoute,
      } = useSimpleSearchRouteParams();

      query.value = "test search";
      documentKind.value = DocumentKind.CaseLaw;
      court.value = "BGH";
      typeGroup.value = "example-type-group";
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
          query: "test%20search",
          court: "BGH",
          documentKind: "R",
          typeGroup: "example-type-group",
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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { query, saveFilterStateToRoute } = useSimpleSearchRouteParams();

      query.value = "DATUM:>2021-01-01";
      await saveFilterStateToRoute();

      expect(navigateToMock).toHaveBeenCalledWith({
        query: expect.objectContaining({
          query: encodeURIComponent("DATUM:>2021-01-01"),
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

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { dateFilter, saveFilterStateToRoute } =
        useSimpleSearchRouteParams();

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

  describe("document kind change side effects", () => {
    it("resets dateFilter when changing document kind", async () => {
      const routeQuery = reactive({
        query: {
          documentKind: DocumentKind.CaseLaw,
          dateFilterType: "period",
          dateFilterFrom: "2024-01-01",
          dateFilterTo: "2024-12-31",
        },
      });

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue(routeQuery),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { documentKind, dateFilter } = useSimpleSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.CaseLaw);
      expect(dateFilter.value.type).toBe("period");

      documentKind.value = DocumentKind.Norm;

      expect(dateFilter.value.type).toBe("allTime");
    });

    it("resets typeGroup and court when changing from CaseLaw", async () => {
      const routeQuery = reactive({
        query: {
          documentKind: DocumentKind.CaseLaw,
          typeGroup: "urteil",
          court: "BGH",
        },
      });

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue(routeQuery),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { documentKind, typeGroup, court } = useSimpleSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.CaseLaw);
      expect(typeGroup.value).toBe("urteil");
      expect(court.value).toBe("BGH");

      documentKind.value = DocumentKind.All;

      expect(typeGroup.value).toBeUndefined();
      expect(court.value).toBeUndefined();
    });

    it("resets all CaseLaw-specific filters when changing from CaseLaw to All", async () => {
      const routeQuery = reactive({
        query: {
          documentKind: DocumentKind.CaseLaw,
          typeGroup: "urteil",
          court: "BGH",
          dateFilterType: "period",
          dateFilterFrom: "2024-01-01",
          dateFilterTo: "2024-12-31",
        },
      });

      vi.doMock("#app", () => ({
        useRoute: vi.fn().mockReturnValue(routeQuery),
      }));

      const { useSimpleSearchRouteParams } =
        await import("./useSimpleSearchRouteParams");

      const { documentKind, typeGroup, court, dateFilter } =
        useSimpleSearchRouteParams();

      expect(documentKind.value).toEqual(DocumentKind.CaseLaw);
      expect(typeGroup.value).toBe("urteil");
      expect(court.value).toBe("BGH");
      expect(dateFilter.value.type).toBe("period");

      documentKind.value = DocumentKind.All;

      expect(typeGroup.value).toBeUndefined();
      expect(court.value).toBeUndefined();
      expect(dateFilter.value.type).toBe("allTime");
    });
  });
});
