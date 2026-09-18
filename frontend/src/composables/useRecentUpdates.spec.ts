import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import type { Ref } from "vue";
import { ref } from "vue";
import type { AnyDocument, SearchResult } from "~/types/api";
import { DocumentKind } from "~/types/api";
import { isLegislation } from "~/utils/anyDocument";
import {
  RECENT_UPDATES_DOCUMENT_KINDS,
  useRecentUpdates,
} from "./useRecentUpdates";

/** Fixed "today" so that the distance to the current date is deterministic. */
const TODAY = "2026-08-28";

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

/**
 * A legislation result entering into force on the given date, i.e. the date the
 * backend sorts norms by.
 */
function norm(entryIntoForce: string) {
  return {
    item: {
      "@type": "Legislation",
      name: entryIntoForce,
      temporalCoverage: `${entryIntoForce}/..`,
    },
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

/** The two halves of the norm window are told apart by their sort order. */
function isPastNormCall(options: MockedOptions) {
  return options.query.sort === "-date";
}

/** Mocks the norms searches, leaving the other document kinds untouched. */
function mockNorms(
  past: SearchResult<AnyDocument>[],
  future: SearchResult<AnyDocument>[],
) {
  useRisBackendMock.mockImplementation((url, options) => {
    if (!url.endsWith("/legislation")) return mockRequest([result(url)]);
    return mockRequest(isPastNormCall(options) ? past : future);
  });
}

const normCalls = () =>
  useRisBackendMock.mock.calls.filter((call) =>
    call[0].endsWith("/legislation"),
  );

const pastNormCall = () => normCalls().find((call) => isPastNormCall(call[1]))!;
const futureNormCall = () =>
  normCalls().find((call) => !isPastNormCall(call[1]))!;

/** The entry-into-force dates of the norms shown, in the order they appear. */
const normDates = (searches: Awaited<ReturnType<typeof useRecentUpdates>>) =>
  searches[0]!.searchResults.value.map((r) =>
    isLegislation(r.item) ? r.item.name : undefined,
  );

const otherCalls = () =>
  useRisBackendMock.mock.calls.filter(
    (call) => !call[0].endsWith("/legislation"),
  );

describe("useRecentUpdates", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.useFakeTimers();
    vi.setSystemTime(new Date(`${TODAY}T12:00:00Z`));
    useRisBackendMock.mockImplementation((url) => mockRequest([result(url)]));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("issues one search per document kind, and two for norms", async () => {
    const searches = await useRecentUpdates();

    expect(useRisBackendMock).toHaveBeenCalledTimes(5);
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
      "/v1/document/lucene-search/legislation",
      "/v1/document/lucene-search/case-law",
      "/v1/document/lucene-search/administrative-directive",
      "/v1/document/lucene-search/literature",
    ]);
  });

  it("requests the five most recent results per document kind", async () => {
    await useRecentUpdates();

    for (const call of otherCalls()) {
      expect(call[1].query).toMatchObject({
        size: 5,
        sort: "-date",
        pageIndex: 0,
      });
    }
  });

  it("requests the five norms entering into force most recently", async () => {
    await useRecentUpdates();

    expect(pastNormCall()[1].query).toMatchObject({
      query: "(DATUM:[now-10d/d TO now/d])",
      size: 5,
      sort: "-date",
      pageIndex: 0,
    });
  });

  it("requests the five norms entering into force next", async () => {
    await useRecentUpdates();

    expect(futureNormCall()[1].query).toMatchObject({
      query: "(DATUM:[now+1d/d TO now+10d/d])",
      size: 5,
      sort: "date",
      pageIndex: 0,
    });
  });

  it("submits an empty query for the other document kinds", async () => {
    await useRecentUpdates();

    for (const call of otherCalls()) {
      expect(call[1].query.query).toBe("");
    }
  });

  it("uses a unique payload key per search", async () => {
    await useRecentUpdates();

    const keys = useRisBackendMock.mock.calls.map((call) => call[1].key);
    expect(new Set(keys).size).toBe(5);
  });

  it("exposes the results of each search", async () => {
    const searches = await useRecentUpdates();

    expect(searches[1]!.searchResults.value).toEqual([
      result("/v1/document/lucene-search/case-law"),
    ]);
  });

  it("falls back to an empty list when a search returns no data", async () => {
    useRisBackendMock.mockImplementation(() => mockRequest(null));

    const searches = await useRecentUpdates();

    expect(searches[1]!.searchResults.value).toEqual([]);
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
    }
  });

  describe("norms", () => {
    it("keeps the norms closest to the current date", async () => {
      mockNorms(
        // Nearest past first, as returned by the descending search
        [
          "2026-08-27",
          "2026-08-25",
          "2026-08-22",
          "2026-08-20",
          "2026-08-18",
        ].map(norm),
        // Nearest future first, as returned by the ascending search
        [
          "2026-08-29",
          "2026-08-30",
          "2026-09-01",
          "2026-09-04",
          "2026-09-07",
        ].map(norm),
      );

      const searches = await useRecentUpdates();

      expect(normDates(searches)).toEqual([
        "2026-09-01",
        "2026-08-30",
        "2026-08-29",
        "2026-08-27",
        "2026-08-25",
      ]);
    });

    it("keeps norms from the past when the future ones are much closer", async () => {
      // The situation on a normal day: entry into force clusters on the first
      // of the month, so without the reserved slots the list would show five
      // norms from the same future date and none that are already in force.
      mockNorms(
        ["2026-08-19", "2026-08-19", "2026-08-18"].map(norm),
        ["2026-09-01", "2026-09-01", "2026-09-01", "2026-09-01"].map(norm),
      );

      const searches = await useRecentUpdates();

      expect(normDates(searches)).toEqual([
        "2026-09-01",
        "2026-09-01",
        "2026-09-01",
        "2026-08-19",
        "2026-08-19",
      ]);
    });

    it("keeps norms from the future when the past ones are much closer", async () => {
      mockNorms(
        ["2026-08-28", "2026-08-28", "2026-08-28", "2026-08-28"].map(norm),
        ["2026-09-01", "2026-09-01", "2026-09-01"].map(norm),
      );

      const searches = await useRecentUpdates();

      expect(normDates(searches)).toEqual([
        "2026-09-01",
        "2026-09-01",
        "2026-08-28",
        "2026-08-28",
        "2026-08-28",
      ]);
    });

    it("returns all norms in the past when there are no future ones", async () => {
      mockNorms(
        [
          "2026-08-27",
          "2026-08-25",
          "2026-08-22",
          "2026-08-20",
          "2026-08-18",
        ].map(norm),
        [],
      );

      const searches = await useRecentUpdates();

      expect(normDates(searches)).toEqual([
        "2026-08-27",
        "2026-08-25",
        "2026-08-22",
        "2026-08-20",
        "2026-08-18",
      ]);
    });

    it("returns all norms in the future when there are no past ones", async () => {
      mockNorms(
        [],
        [
          "2026-08-29",
          "2026-08-30",
          "2026-09-01",
          "2026-09-04",
          "2026-09-07",
        ].map(norm),
      );

      const searches = await useRecentUpdates();

      expect(normDates(searches)).toEqual([
        "2026-09-07",
        "2026-09-04",
        "2026-09-01",
        "2026-08-30",
        "2026-08-29",
      ]);
    });

    it("returns fewer results when the window holds fewer norms", async () => {
      mockNorms([norm("2026-08-26")], [norm("2026-08-31")]);

      const searches = await useRecentUpdates();

      expect(normDates(searches)).toEqual(["2026-08-31", "2026-08-26"]);
    });

    it("returns nothing when no norms enter into force within the window", async () => {
      mockNorms([], []);

      const searches = await useRecentUpdates();

      expect(normDates(searches)).toEqual([]);
    });

    it("reports an error when either half of the window fails", async () => {
      const failure = new Error("Search failed");

      useRisBackendMock.mockImplementation((url, options) =>
        url.endsWith("/legislation") && !isPastNormCall(options)
          ? mockRequest(null, failure)
          : mockRequest([result(url)]),
      );

      const searches = await useRecentUpdates();

      expect(searches[0]!.searchError.value).toBe(failure);
    });
  });
});
