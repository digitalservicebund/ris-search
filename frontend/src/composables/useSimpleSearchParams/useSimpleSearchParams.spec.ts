import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { defaultParams } from "./getInitialState";
import {
  type QueryParams,
  useSimpleSearchParams,
} from "./useSimpleSearchParams";
import type { RouteLocationNormalized } from "#vue-router";
import { DocumentKind } from "~/types";
import { sortMode } from "~/utils/search/sortMode";

type MockRoute = Pick<RouteLocationNormalized, "path" | "query">;
const mockRouteInitialState = {
  path: "/search",
  query: {},
};
const mockRoute = ref<MockRoute>(mockRouteInitialState);
const routerEmptyState = {
  push: (newValue: MockRoute) => {
    mockRoute.value = newValue;
  },
};

const mockPostHog = {
  searchPerformed: vi.fn(),
};

// refer to https://nuxt.com/docs/getting-started/testing#unit-testing
const { useRouteMock, useRouterMock, usePostHogMock } = vi.hoisted(() => {
  return {
    useRouteMock: vi.fn().mockImplementation(() => mockRoute.value),
    useRouterMock: vi.fn().mockImplementation(() => routerEmptyState),
    usePostHogMock: () => mockPostHog,
  };
});

mockNuxtImport("useRoute", () => {
  return useRouteMock;
});
mockNuxtImport("useRouter", () => {
  return useRouterMock;
});

vi.mock("~/composables/usePostHog", () => {
  return {
    usePostHog: usePostHogMock,
  };
});

describe("useSimpleSearchParams", () => {
  beforeEach(async () => {
    mockRoute.value = mockRouteInitialState;
    mockPostHog.searchPerformed.mockRestore();
  });

  it("returns default parameters if the URL is empty", async () => {
    const params = useSimpleSearchParams();

    expect(params.query.value).toBeFalsy();
    expect(params.pageIndex.value).toBe(0);
    expect(params.category.value).toBe(defaultParams.category);
    expect(params.sort.value).toBe(defaultParams.sort);

    expect(params.date.value).toBeUndefined();
    expect(params.dateAfter.value).toBeUndefined();
    expect(params.dateBefore.value).toBeUndefined();
    expect(params.dateSearchMode.value).toBeFalsy();
  });

  it("returns the correct category if one is set", async () => {
    useRouteMock.mockReturnValueOnce({ query: { category: "R" } });

    const params = useSimpleSearchParams();

    expect(params.category.value).toEqual("R");
  });

  for (const category of ["A", "R", "N", "R.urteil"])
    it(`returns the correct category for ${category}`, async () => {
      useRouteMock.mockReturnValueOnce({
        query: { category },
      });

      const params = useSimpleSearchParams();

      expect(params.category.value).toEqual(category);
    });

  it("discards caseLaw-specific options if the DocumentKind is updated", async () => {
    const mockPush = vi.fn();

    useRouteMock.mockReturnValueOnce({
      query: {
        category: DocumentKind.CaseLaw,
        sort: sortMode.courtName,
        court: "Example court",
      },
    });
    useRouterMock.mockReturnValue({
      push: mockPush,
    });

    const params = useSimpleSearchParams();
    expect(params.category.value).toEqual("R");
    expect(params.sort.value).toEqual(sortMode.courtName);
    expect(params.court.value).toEqual("Example court");

    params.category.value = DocumentKind.Norm;
    await nextTick();

    expect(params.sort.value).toEqual(sortMode.default);
    expect(params.court.value).toBeUndefined();
  });

  it("discards typeGroup when changing from CaseLaw subtype to All", async () => {
    const mockPush = vi.fn();

    useRouteMock.mockReturnValueOnce({
      query: {
        category: "R.urteil",
        court: "BGH",
      },
    });
    useRouterMock.mockReturnValue({
      push: mockPush,
    });

    const params = useSimpleSearchParams();
    expect(params.category.value).toEqual("R.urteil");
    expect(params.court.value).toEqual("BGH");

    params.category.value = DocumentKind.All;
    await nextTick();

    expect(params.category.value).toEqual(DocumentKind.All);
    expect(params.court.value).toBeUndefined();
  });

  it("discards court and sort when changing from CaseLaw with typeGroup to Literature", async () => {
    const mockPush = vi.fn();

    useRouteMock.mockReturnValueOnce({
      query: {
        category: "R.beschluss",
        sort: sortMode.courtName,
        court: "LG Hamburg",
      },
    });
    useRouterMock.mockReturnValue({
      push: mockPush,
    });

    const params = useSimpleSearchParams();
    expect(params.category.value).toEqual("R.beschluss");
    expect(params.sort.value).toEqual(sortMode.courtName);
    expect(params.court.value).toEqual("LG Hamburg");

    params.category.value = DocumentKind.Literature;
    await nextTick();

    expect(params.category.value).toEqual(DocumentKind.Literature);
    expect(params.sort.value).toEqual(sortMode.default);
    expect(params.court.value).toBeUndefined();
  });

  it("preserves date filter when changing document kinds", async () => {
    const mockPush = vi.fn();

    useRouteMock.mockReturnValueOnce({
      query: {
        category: DocumentKind.CaseLaw,
        dateAfter: "2024-01-01",
        dateBefore: "2024-12-31",
      },
    });
    useRouterMock.mockReturnValue({
      push: mockPush,
    });

    const params = useSimpleSearchParams();
    expect(params.category.value).toEqual(DocumentKind.CaseLaw);
    expect(params.dateAfter.value).toEqual("2024-01-01");
    expect(params.dateBefore.value).toEqual("2024-12-31");

    params.category.value = DocumentKind.Literature;
    await nextTick();

    expect(params.category.value).toEqual(DocumentKind.Literature);
    expect(params.dateAfter.value).toEqual("2024-01-01");
    expect(params.dateBefore.value).toEqual("2024-12-31");
  });

  it("resets the page index if the category is updated", async () => {
    const mockPush = vi.fn();
    useRouterMock.mockReturnValueOnce({
      push: mockPush,
    });
    useRouteMock.mockReturnValueOnce({
      query: {
        category: DocumentKind.CaseLaw,
        pageIndex: 1,
      },
    });

    const params = useSimpleSearchParams();
    expect(params.pageIndex.value).toEqual(1);

    params.category.value = DocumentKind.Norm;
    await nextTick();

    expect(params.pageIndex.value).toEqual(0);
  });

  it("updates the URL", async () => {
    const mockPush = vi
      .fn()
      .mockImplementation((newValue) => Object.assign(mockRoute, newValue));

    useRouterMock.mockReturnValue({
      push: mockPush,
    });

    const params = useSimpleSearchParams();

    params.category.value = DocumentKind.CaseLaw;
    params.pageIndex.value = 1;
    await nextTick();
    expect(mockPush).toHaveBeenLastCalledWith({
      path: "/search",
      query: { category: DocumentKind.CaseLaw, pageIndex: "1" },
    });

    params.category.value = DocumentKind.All;
    await nextTick();
    expect(mockPush).toHaveBeenLastCalledWith({
      path: "/search",
      query: {}, // pageIndex is reset through category change
    });
  });

  it("updates if the URL changes, and reports an event", async () => {
    const mockRoute = reactive({
      query: { query: "first" },
    });
    useRouteMock.mockReturnValue(mockRoute);
    const params = useSimpleSearchParams();
    expect(params.query.value).toBe("first");

    mockRoute.query = { query: "second" };
    await nextTick();

    expect(params.query.value).toBe("second");

    expect(mockPostHog.searchPerformed).toHaveBeenCalledExactlyOnceWith(
      "simple",
      {
        category: "A",
        dateSearchMode: "",
        itemsPerPage: 10,
        pageIndex: 0,
        query: "second",
        sort: "default",
      },
      {
        category: "A",
        dateSearchMode: "",
        itemsPerPage: 10,
        pageIndex: 0,
        query: "first",
        sort: "default",
      },
    );
  });

  it("does not create a reactive cycle", async () => {
    const mockRoute = reactive<{ query: Partial<QueryParams> }>({
      query: { query: "testQuery" },
    });
    useRouteMock.mockReturnValue(mockRoute);
    const mockPush = vi
      .fn()
      .mockImplementation((newValue) => Object.assign(mockRoute, newValue));
    useRouterMock.mockReturnValue({
      push: mockPush,
    });

    const params = useSimpleSearchParams();
    params.sort.value = "newSort";
    await nextTick();
    expect(mockPush).toHaveBeenCalledExactlyOnceWith({
      query: { query: "testQuery", sort: "newSort" },
    });
    expect(mockRoute.query.sort).toBe("newSort");
  });
});
