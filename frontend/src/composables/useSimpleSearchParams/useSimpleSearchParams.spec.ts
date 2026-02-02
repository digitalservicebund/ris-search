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
    expect(params.pageNumber.value).toBe(0);
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

    const params = useSimpleSearchParams();
    expect(params.pageNumber.value).toEqual(1);

    params.category.value = DocumentKind.Norm;
    await nextTick();

    expect(params.pageNumber.value).toEqual(0);
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
    params.pageNumber.value = 1;
    await nextTick();
    expect(mockPush).toHaveBeenLastCalledWith({
      path: "/search",
      query: { category: DocumentKind.CaseLaw, pageNumber: "1" },
    });

    params.category.value = DocumentKind.All;
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

    const params = useSimpleSearchParams();
    params.sort.value = "newSort";
    await nextTick();
    expect(mockPush).toHaveBeenCalledExactlyOnceWith({
      query: { query: "testQuery", sort: "newSort" },
    });
    expect(mockRoute.query.sort).toBe("newSort");
  });
});
