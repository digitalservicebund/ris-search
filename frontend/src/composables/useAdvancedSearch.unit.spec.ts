import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { useAdvancedSearch } from "./useAdvancedSearch";
import { DocumentKind } from "~/types";

const { useFetchMock, executeMock } = vi.hoisted(() => {
  const executeMock = vi.fn();

  return {
    /* eslint-disable-next-line @typescript-eslint/no-explicit-any -- Simplified for testing */
    useFetchMock: vi.fn((_url: Ref<string>, _opts: Record<string, any>) => ({
      status: ref("success"),
      data: computed(() => ref({ content: [], totalItems: 0 })),
      error: ref(null),
      pending: ref(false),
      execute: executeMock,
      refresh: vi.fn(),
      clear: vi.fn(),
    })),
    executeMock,
  };
});

mockNuxtImport("useFetch", () => {
  return useFetchMock;
});

describe("useAdvancedSearch", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("calls the endpoint for case law with correct URL", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      {},
    );

    expect(useFetchMock).toHaveBeenCalled();
    const url = useFetchMock.mock.calls[0]![0];
    expect(url.value).toBe("/v1/document/lucene-search/case-law");
  });

  it("calls the endpoint for legislation with correct URL", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.Norm,
      { type: "allTime" },
      {},
    );

    expect(useFetchMock).toHaveBeenCalled();
    const url = useFetchMock.mock.calls[0]![0];
    expect(url.value).toBe("/v1/document/lucene-search/legislation");
  });

  it("defaults to document search for other document kinds", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.All,
      { type: "allTime" },
      {},
    );

    expect(useFetchMock).toHaveBeenCalled();
    const url = useFetchMock.mock.calls[0]![0];
    expect(url.value).toBe("/v1/document/lucene-search");
  });

  it("submits the query string", async () => {
    await useAdvancedSearch(
      "test query",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      {},
    );

    expect(useFetchMock).toHaveBeenCalled();
    const urlQuery = useFetchMock.mock.calls[0]![1].query;
    expect(urlQuery.value).toMatchObject({ query: "test query" });
  });

  it("submits pagination parameters correctly", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      { itemsPerPage: "25", pageIndex: 2 },
    );

    expect(useFetchMock).toHaveBeenCalled();
    const urlQuery = useFetchMock.mock.calls[0]![1].query;
    expect(urlQuery.value).toMatchObject({ size: "25", pageIndex: 2 });
  });

  it("submits sort order parameter correctly", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      { sort: "relevance" },
    );

    expect(useFetchMock).toHaveBeenCalled();
    const urlQuery = useFetchMock.mock.calls[0]![1].query;
    expect(urlQuery.value).toMatchObject({ sort: "relevance" });
  });

  it("does not eagerly execute the query", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      {},
    );

    expect(useFetchMock).toHaveBeenCalled();
    const immediate = useFetchMock.mock.calls[0]![1].immediate;
    expect(immediate).toBe(false);
  });

  it("does not refetch on changes", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      {},
    );

    expect(useFetchMock).toHaveBeenCalled();
    const watch = useFetchMock.mock.calls[0]![1].watch;
    expect(watch).toBe(false);
  });

  it("does not submit with an empty query string", async () => {
    const { submitSearch } = await useAdvancedSearch(
      "",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      {},
    );

    await submitSearch();

    expect(executeMock).not.toHaveBeenCalled();
  });

  it("submits with a valid query string", async () => {
    const { submitSearch } = await useAdvancedSearch(
      "test query",
      DocumentKind.CaseLaw,
      { type: "allTime" },
      {},
    );

    await submitSearch();

    expect(executeMock).toHaveBeenCalled();
  });
});
