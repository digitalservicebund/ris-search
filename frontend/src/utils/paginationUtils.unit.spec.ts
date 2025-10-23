import { describe, it, expect } from "vitest";
import type { Page } from "~/components/Pagination/Pagination.vue";
import type { AnyDocument, SearchResult } from "~/types";
import { buildItemsOnPageString } from "~/utils/paginationUtils";

function getPage(params: {
  page: number;
  size: number;
  totalItems: number;
  itemsOnPage: number;
}): Page {
  return {
    "@id": `list?pageIndex=${params.page}&size=${params.size}`,
    totalItems: params.totalItems,
    member: Array(params.itemsOnPage) as SearchResult<AnyDocument>[],
    view: {},
  };
}

describe("buildItemsOnPageString", () => {
  it("correctly describes a single hit", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 0, size: 10, itemsOnPage: 1, totalItems: 1 }),
    );
    expect(string).toBe("Treffer 1 von 1");
  });
  it("correctly describes a full first page", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 0, size: 10, itemsOnPage: 10, totalItems: 100 }),
    );
    expect(string).toBe("Treffer 1–10 von 100");
  });
  it("correctly describes a full second page", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 1, size: 10, itemsOnPage: 10, totalItems: 100 }),
    );
    expect(string).toBe("Treffer 11–20 von 100");
  });
  it("correctly describes a partial first page", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 0, size: 10, itemsOnPage: 5, totalItems: 5 }),
    );
    expect(string).toBe("Treffer 1–5 von 5");
  });
  it("correctly describes a full second page", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 1, size: 10, itemsOnPage: 5, totalItems: 15 }),
    );
    expect(string).toBe("Treffer 11–15 von 15");
  });
  it("correctly describes a custom page", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 1, size: 3, itemsOnPage: 2, totalItems: 5 }),
    );
    expect(string).toBe("Treffer 4–5 von 5");
  });
  it("correctly describes the last item on its own page", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 1, size: 10, itemsOnPage: 1, totalItems: 11 }),
    );
    expect(string).toBe("Treffer 11 von 11");
  });
  it("correctly describes more than 10000 items", () => {
    const string = buildItemsOnPageString(
      getPage({ page: 0, size: 10, itemsOnPage: 10, totalItems: 10000 }),
    );
    expect(string).toBe("Treffer 1–10 von mehr als 10.000");
  });
});
