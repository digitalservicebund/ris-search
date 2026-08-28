import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { Ref } from "vue";
import { ref } from "vue";
import type { AnyDocument, SearchResult } from "~/types/api";
import { DocumentKind } from "~/types/api";
import {
  RECENT_UPDATES_DOCUMENT_KINDS,
  useRecentUpdates,
} from "./useRecentUpdates";

type MockedOptions = {
  key: string;
  query: { query: string; size: number; sort: string; pageIndex: number };
};

type MockedRequest = {
  data: Ref<{ member: SearchResult<AnyDocument>[] } | null>;
  error: Ref<Error | null>;
  status: Ref<string>;
  execute: () => void;
  refresh: () => void;
  clear: () => void;
};

function result(name: string) {
  return {
    item: { name },
    textMatches: [],
  } as unknown as SearchResult<AnyDocument>;
}

function mockRequest(
  member: SearchResult<AnyDocument>[] | null,
  error: Error | null = null,
): MockedRequest {
  return {
    data: ref(member === null ? null : { member }),
    error: ref(error),
    status: ref("success"),
    execute: vi.fn(),
    refresh: vi.fn(),
    clear: vi.fn(),
  };
}

const { useRisBackendMock } = vi.hoisted(() => ({
  useRisBackendMock: vi.fn(
    (url: string, _options: MockedOptions): MockedRequest =>
      mockRequest([result(url)]),
  ),
}));

mockNuxtImport("useRisBackend", () => useRisBackendMock);

describe("useRecentUpdates", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useRisBackendMock.mockImplementation((url) => mockRequest([result(url)]));
  });

  it("issues one search per document kind", async () => {
    const searches = await useRecentUpdates();

    expect(useRisBackendMock).toHaveBeenCalledTimes(4);
    expect(RECENT_UPDATES_DOCUMENT_KINDS).toHaveLength(4);
    expect(searches.map((s) => s.documentKind)).toEqual([
      DocumentKind.Norm,
      DocumentKind.CaseLaw,
      DocumentKind.AdministrativeDirective,
      DocumentKind.Literature,
    ]);
  });

  it("calls the lucene endpoint matching each document kind", async () => {
    await useRecentUpdates();

    expect(useRisBackendMock.mock.calls.map((call) => call[0])).toEqual([
      "/v1/document/lucene-search/legislation",
      "/v1/document/lucene-search/case-law",
      "/v1/document/lucene-search/administrative-directive",
      "/v1/document/lucene-search/literature",
    ]);
  });

  it("requests five results per document kind with the default sort", async () => {
    await useRecentUpdates();

    for (const call of useRisBackendMock.mock.calls) {
      expect(call[1].query).toMatchObject({
        size: 5,
        sort: "default",
        pageIndex: 0,
      });
    }
  });

  it("filters legislation down to the currently valid versions", async () => {
    await useRecentUpdates();

    expect(useRisBackendMock.mock.calls[0]![1].query.query).toMatch(
      /^\(entry_into_force_date:<\d{4}-\d{2}-\d{2} AND \(\(expiry_date:>\d{4}-\d{2}-\d{2}\) OR \(NOT _exists_:expiry_date\)\)\)$/,
    );
  });

  it("submits an empty query for the other document kinds", async () => {
    await useRecentUpdates();

    for (const call of useRisBackendMock.mock.calls.slice(1)) {
      expect(call[1].query.query).toBe("");
    }
  });

  it("uses a unique payload key per document kind", async () => {
    await useRecentUpdates();

    const keys = useRisBackendMock.mock.calls.map((call) => call[1].key);
    expect(new Set(keys).size).toBe(4);
  });

  it("exposes the results of each search", async () => {
    const searches = await useRecentUpdates();

    expect(searches[0]!.searchResults.value).toEqual([
      result("/v1/document/lucene-search/legislation"),
    ]);
  });

  it("falls back to an empty list when a search returns no data", async () => {
    useRisBackendMock.mockImplementation(() => mockRequest(null));

    const searches = await useRecentUpdates();

    expect(searches[0]!.searchResults.value).toEqual([]);
  });

  it("reports an error on the failing document kind only", async () => {
    const failure = new Error("Search failed");

    useRisBackendMock.mockImplementation((url) =>
      url.endsWith("/case-law")
        ? mockRequest(null, failure)
        : mockRequest([result("Some document")]),
    );

    const searches = await useRecentUpdates();

    const caseLaw = searches.find(
      (s) => s.documentKind === DocumentKind.CaseLaw,
    );
    expect(caseLaw!.searchError.value).toBe(failure);
    expect(caseLaw!.searchResults.value).toEqual([]);

    for (const other of searches.filter(
      (s) => s.documentKind !== DocumentKind.CaseLaw,
    )) {
      expect(other.searchError.value).toBeNull();
      expect(other.searchResults.value).toEqual([result("Some document")]);
    }
  });
});
