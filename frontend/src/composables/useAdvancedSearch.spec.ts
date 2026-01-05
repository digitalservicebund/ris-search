import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { useAdvancedSearch } from "./useAdvancedSearch";
import { DocumentKind } from "~/types";

const { useRisBackendMock, executeMock } = vi.hoisted(() => {
  const executeMock = vi.fn();

  return {
    useRisBackendMock: vi.fn(
      (_url: Ref<string>, _opts: Record<string, Ref<string>>) => ({
        status: ref("success"),
        data: computed(() => ref({ content: [], totalItems: 0 })),
        error: ref(null),
        pending: ref(false),
        execute: executeMock,
        refresh: vi.fn(),
        clear: vi.fn(),
      }),
    ),
    executeMock,
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

describe("useAdvancedSearch", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("calls the endpoint for case law with correct URL", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const url = useRisBackendMock.mock.calls[0]![0];
    expect(url.value).toBe("/v1/document/lucene-search/case-law");
  });

  it("calls the endpoint for legislation with correct URL", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.Norm,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const url = useRisBackendMock.mock.calls[0]![0];
    expect(url.value).toBe("/v1/document/lucene-search/legislation");
  });

  it("calls the endpoint for literature with correct URL", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.Literature,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const url = useRisBackendMock.mock.calls[0]![0];
    expect(url.value).toBe("/v1/document/lucene-search/literature");
  });

  it("calls the endpoint for administrative directive with correct URL", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.AdministrativeDirective,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const url = useRisBackendMock.mock.calls[0]![0];
    expect(url.value).toBe(
      "/v1/document/lucene-search/administrative-directive",
    );
  });

  it("defaults to document search for other document kinds", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.All,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const url = useRisBackendMock.mock.calls[0]![0];
    expect(url.value).toBe("/v1/document/lucene-search");
  });

  it("submits the query string", async () => {
    await useAdvancedSearch(
      "test query",
      DocumentKind.CaseLaw,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const urlQuery = useRisBackendMock.mock.calls[0]![1].query;
    expect(urlQuery?.value).toMatchObject({ query: "(test query)" });
  });

  it("submits pagination parameters correctly", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime", from: undefined, to: undefined },
      { itemsPerPage: "25", pageIndex: 2 },
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const urlQuery = useRisBackendMock.mock.calls[0]![1].query;
    expect(urlQuery?.value).toMatchObject({ size: "25", pageIndex: 2 });
  });

  it("submits sort order parameter correctly", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime", from: undefined, to: undefined },
      { sort: "relevance" },
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const urlQuery = useRisBackendMock.mock.calls[0]![1].query;
    expect(urlQuery?.value).toMatchObject({ sort: "relevance" });
  });

  it("does not eagerly execute the query", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const immediate = useRisBackendMock.mock.calls[0]![1].immediate;
    expect(immediate).toBe(false);
  });

  it("does not refetch on changes", async () => {
    await useAdvancedSearch(
      "example",
      DocumentKind.CaseLaw,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    expect(useRisBackendMock).toHaveBeenCalled();
    const watch = useRisBackendMock.mock.calls[0]![1].watch;
    expect(watch).toBe(false);
  });

  it("submits the query", async () => {
    const { submitSearch } = await useAdvancedSearch(
      "test query",
      DocumentKind.CaseLaw,
      { type: "allTime", from: undefined, to: undefined },
      {},
    );

    await submitSearch();

    expect(executeMock).toHaveBeenCalled();
  });
});
