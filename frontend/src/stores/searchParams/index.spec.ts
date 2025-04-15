import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { useSimpleSearchParamsStore } from "@/stores/searchParams/index";
import { DocumentKind } from "@/types";
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { defaultParams } from "@/stores/searchParams/getInitialState";
import { sortMode } from "@/components/types";

const routerEmptyState = { currentRoute: { value: { query: {} } } };

// refer to https://nuxt.com/docs/getting-started/testing#unit-testing
const { useRouterMock } = vi.hoisted(() => {
  return {
    useRouterMock: vi.fn().mockImplementation(() => routerEmptyState),
  };
});

mockNuxtImport("useRouter", () => {
  return useRouterMock;
});

describe("useSimpleSearchParamsStore", () => {
  beforeEach(async () => {
    setActivePinia(createPinia());
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
    useRouterMock.mockReturnValueOnce({
      currentRoute: { value: { query: { category: "R" } } },
    });

    const store = useSimpleSearchParamsStore();

    expect(store.category).toEqual("R");
  });

  for (const category of ["A", "R", "N", "R.urteil"])
    it(`returns the correct category for ${category}`, async () => {
      useRouterMock.mockReturnValueOnce({
        currentRoute: { value: { query: { category } } },
      });

      const store = useSimpleSearchParamsStore();

      expect(store.category).toEqual(category);
    });

  it("discards caseLaw-specific options if the DocumentKind is updated", async () => {
    const mockReplace = vi.fn();
    useRouterMock.mockReturnValue({
      currentRoute: {
        value: {
          query: {
            category: DocumentKind.CaseLaw,
            sort: sortMode.courtName,
            court: "Example court",
          },
        },
      },
      replace: mockReplace,
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
    const mockReplace = vi.fn();
    useRouterMock.mockReturnValue({
      currentRoute: {
        value: {
          query: {
            category: DocumentKind.CaseLaw,
            pageNumber: 1,
          },
        },
      },
      replace: mockReplace,
    });

    const store = useSimpleSearchParamsStore();
    expect(store.pageNumber).toEqual(1);

    store.category = DocumentKind.Norm;
    await nextTick();

    expect(store.pageNumber).toEqual(0);
  });

  it("updates the URL", async () => {
    const mockReplace = vi.fn();
    useRouterMock.mockReturnValue({
      ...routerEmptyState,
      replace: mockReplace,
    });

    const store = useSimpleSearchParamsStore();

    store.category = DocumentKind.CaseLaw;
    await nextTick();
    expect(mockReplace).toHaveBeenLastCalledWith({
      query: { category: DocumentKind.CaseLaw },
    });

    store.category = DocumentKind.All;
    await nextTick();
    expect(mockReplace).toHaveBeenLastCalledWith({
      query: {},
    });
  });
});
