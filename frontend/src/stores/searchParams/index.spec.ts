import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import type { RouteLocationNormalized } from "#vue-router";
import { sortMode } from "~/components/types";
import { defaultParams } from "~/stores/searchParams/getInitialState";
import {
  type QueryParams,
  useSimpleSearchParamsStore,
} from "~/stores/searchParams/index";
import { DocumentKind } from "~/types";

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
const { useRouteMock, useRouterMock, usePostHogStoreMock } = vi.hoisted(() => {
  return {
    useRouteMock: vi.fn().mockImplementation(() => mockRoute.value),
    useRouterMock: vi.fn().mockImplementation(() => routerEmptyState),
    usePostHogStoreMock: () => mockPostHog,
  };
});

mockNuxtImport("useRoute", () => {
  return useRouteMock;
});
mockNuxtImport("useRouter", () => {
  return useRouterMock;
});
mockNuxtImport("usePostHogStore", () => {
  return usePostHogStoreMock;
});

describe("useSimpleSearchParamsStore", () => {
  beforeEach(async () => {
    setActivePinia(createPinia());
    mockRoute.value = mockRouteInitialState;
    mockPostHog.searchPerformed.mockRestore();
  });

  it("returns default parameters if the URL is empty", async () => {
    const store = useSimpleSearchParamsStore();

    expect(store.query).toBeFalsy();
    expect(store.pageNumber).toBe(0);
    expect(store.category).toBe(defaultParams.category);
    expect(store.sort).toBe(defaultParams.sort);

    expect(store.date).toBeUndefined();
    expect(store.dateAfter).toBeUndefined();
    expect(store.dateBefore).toBeUndefined();
    expect(store.dateSearchMode).toBeFalsy();
  });

  it("returns the correct category if one is set", async () => {
    useRouteMock.mockReturnValueOnce({ query: { category: "R" } });

    const store = useSimpleSearchParamsStore();

    expect(store.category).toEqual("R");
  });

  for (const category of ["A", "R", "N", "R.urteil"])
    it(`returns the correct category for ${category}`, async () => {
      useRouteMock.mockReturnValueOnce({
        query: { category },
      });

      const store = useSimpleSearchParamsStore();

      expect(store.category).toEqual(category);
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

    const store = useSimpleSearchParamsStore();
    expect(store.category).toEqual("R");
    expect(store.sort).toEqual(sortMode.courtName);
    expect(store.court).toEqual("Example court");

    store.category = DocumentKind.Norm;
    await nextTick();

    expect(store.sort).toEqual(sortMode.default);
    expect(store.court).toBeUndefined();
  });

  it("resets the page index if the category is updated", async () => {
    const mockPush = vi.fn();
    useRouterMock.mockReturnValueOnce({
      push: mockPush,
    });
    useRouteMock.mockReturnValueOnce({
      query: {
        category: DocumentKind.CaseLaw,
        pageNumber: 1,
      },
    });

    const store = useSimpleSearchParamsStore();
    expect(store.pageNumber).toEqual(1);

    store.category = DocumentKind.Norm;
    await nextTick();

    expect(store.pageNumber).toEqual(0);
  });

  it("updates the URL", async () => {
    const mockPush = vi
      .fn()
      .mockImplementation((newValue) => Object.assign(mockRoute, newValue));

    useRouterMock.mockReturnValue({
      push: mockPush,
    });

    const store = useSimpleSearchParamsStore();

    store.category = DocumentKind.CaseLaw;
    store.pageNumber = 1;
    await nextTick();
    expect(mockPush).toHaveBeenLastCalledWith({
      path: "/search",
      query: { category: DocumentKind.CaseLaw, pageNumber: "1" },
    });

    store.category = DocumentKind.All;
    await nextTick();
    expect(mockPush).toHaveBeenLastCalledWith({
      path: "/search",
      query: {}, // pageNumber is reset through category change
    });
  });

  it("updates if the URL changes, and reports an event", async () => {
    const mockRoute = reactive({
      query: { query: "first" },
    });
    useRouteMock.mockReturnValue(mockRoute);
    const store = useSimpleSearchParamsStore();
    expect(store.query).toBe("first");

    mockRoute.query = { query: "second" };
    await nextTick();

    expect(store.query).toBe("second");

    expect(mockPostHog.searchPerformed).toHaveBeenCalledExactlyOnceWith(
      "simple",
      {
        category: "A",
        dateSearchMode: "",
        itemsPerPage: 10,
        pageNumber: 0,
        query: "second",
        sort: "default",
      },
      {
        category: "A",
        dateSearchMode: "",
        itemsPerPage: 10,
        pageNumber: 0,
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

    const store = useSimpleSearchParamsStore();
    store.sort = "newSort";
    await nextTick();
    expect(mockPush).toHaveBeenCalledExactlyOnceWith({
      query: { query: "testQuery", sort: "newSort" },
    });
    expect(mockRoute.query.sort).toBe("newSort");
  });
});
