import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { TranslationContent } from "~/composables/useTranslationData";
import {
  fetchTranslationAndHTML,
  fetchTranslationList,
  fetchTranslationListWithIdFilter,
  getGermanOriginal,
} from "~/composables/useTranslationData";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

const { useRisBackendMock, dataRef } = await vi.hoisted(async () => {
  const { ref } = await import("vue");
  const dataRef = ref(null) as Ref<unknown>;

  return {
    useRisBackendMock: vi.fn(
      (_url: Ref<string>, _opts?: Record<string, Ref<string>>) => ({
        status: ref("success"),
        data: dataRef,
        error: ref(null),
        pending: ref(false),
        execute: vi.fn(),
        refresh: vi.fn(),
        clear: vi.fn(),
      }),
    ),
    dataRef,
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

mockNuxtImport("useNuxtApp", () => {
  const nuxtApp = (globalThis as unknown as Window)?.useNuxtApp?.() ?? {};
  return () => {
    return {
      ...nuxtApp,
      $risBackend: mockFetch,
    };
  };
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe("fetchTranslationList", () => {
  it("returns a list when there is no error", async () => {
    const mockTranslationResponse = [{ "@id": "Cde" }, { "@id": "AbC" }];
    dataRef.value = mockTranslationResponse;
    const { translations, translationsError } = await fetchTranslationList();

    expect(useRisBackendMock).toHaveBeenCalledWith("/v1/translatedLegislation");
    expect(translations.value).toEqual(mockTranslationResponse);
    expect(translationsError.value).toBeNull();
  });
});

describe("fetchTranslationListWithIdFilter", () => {
  it("fetches a filtered list of translation", async () => {
    const mockTranslationResponse = [{ "@id": "Cde" }];
    dataRef.value = mockTranslationResponse;
    const { translations } = await fetchTranslationListWithIdFilter("Cde");
    expect(translations.value).toEqual(mockTranslationResponse);

    expect(useRisBackendMock).toHaveBeenCalledWith(
      "/v1/translatedLegislation?id=Cde",
    );
  });
});

describe("fetchTranslationAndHTML", () => {
  it("fetches translation data and HTML content", async () => {
    const mockTranslationResponse = [
      {
        "@id": "AbC",
        name: "Act A",
        inLanguage: "en",
        translator: "…",
        translationOfWork: "Gesetz A",
        about: "…",
        "ris:filename": "englisch_abc.html",
      },
    ];

    const mockHtmlResponse = "<h1>Mock HTML Content</h1>";

    mockFetch.mockResolvedValueOnce(mockTranslationResponse);
    mockFetch.mockResolvedValueOnce(mockHtmlResponse);

    const { data, error } = await fetchTranslationAndHTML("AbC");

    expect(mockFetch).toHaveBeenCalledWith("/v1/translatedLegislation?id=AbC");

    expect(mockFetch).toHaveBeenCalledWith(
      "/v1/translatedLegislation/englisch_abc.html",
      {
        headers: {
          Accept: "text/html",
        },
      },
    );

    expect(error.value).toBeUndefined();

    expect(data.value?.content).toEqual(mockTranslationResponse[0]);
    expect(data.value?.html).toEqual(mockHtmlResponse);
  });

  it("returns 404 when list is empty", async () => {
    const mockTranslationResponse: TranslationContent[] = [];
    mockFetch.mockResolvedValueOnce(mockTranslationResponse);

    const { data, error } = await fetchTranslationAndHTML("FgH");

    expect(mockFetch).toHaveBeenCalledWith("/v1/translatedLegislation?id=FgH");

    expect(mockFetch).toHaveBeenCalledTimes(1);

    expect(error.value).not.toBeNull();
    expect(error.value?.statusCode).toBe(404);
    expect(error.value?.statusMessage).toBe("Translation not found");

    expect(data.value).toBeUndefined();
  });

  it("returns 404 when there is no entry for ris:filename", async () => {
    const mockTranslationResponse = [
      {
        "@id": "FgH",
        name: "Act F",
        inLanguage: "en",
        translator: "…",
        translationOfWork: "Gesetz F",
        about: "…",
      },
    ];
    mockFetch.mockResolvedValueOnce(mockTranslationResponse);

    const { data, error } = await fetchTranslationAndHTML("FgH");

    expect(mockFetch).toHaveBeenCalledWith("/v1/translatedLegislation?id=FgH");

    expect(mockFetch).toHaveBeenCalledTimes(1);

    expect(error.value).not.toBeNull();
    expect(error.value?.statusCode).toBe(404);
    expect(error.value?.statusMessage).toBe("Translation filename not found");

    expect(data.value).toBeUndefined();
  });
});

describe("getGermanOriginal", () => {
  beforeAll(() => {
    vi.setSystemTime(new Date("2025-10-13T00:00:00.000Z"));
  });

  beforeEach(() => {
    clearNuxtData();
  });

  afterAll(() => {
    vi.useRealTimers();
  });

  it("returns first legislation work when API returns results", async () => {
    const mockResult = { item: { abbreviation: "test-id" } };
    dataRef.value = { member: [mockResult] };

    const { legislation, legislationSearchError } =
      await getGermanOriginal("test-id");

    expect(legislation.value).toEqual(mockResult);
    expect(legislationSearchError.value).toBeNull();
    expect(useRisBackendMock).toHaveBeenCalledWith(
      "/v1/legislation?searchTerm=test-id&temporalCoverageFrom=2025-10-13&temporalCoverageTo=2025-10-13&size=100&pageIndex=0",
    );
  });

  it("returns 404 error when API returns empty member list", async () => {
    dataRef.value = { member: [] };

    const { legislation, legislationSearchError, legislationSearchStatus } =
      await getGermanOriginal("test-id");

    expect(legislation.value).toBeNull();
    expect(legislationSearchError.value).not.toBeNull();
    expect(legislationSearchStatus.value).toBe("404");
    expect(legislationSearchError.value?.message).toBe(
      "The fetched legislation does not match the requested ID: test-id",
    );
  });

  it("returns 404 error when API returns null", async () => {
    dataRef.value = null;

    const { legislation, legislationSearchError, legislationSearchStatus } =
      await getGermanOriginal("test-id");

    expect(legislation.value).toBeNull();
    expect(legislationSearchError.value).not.toBeNull();
    expect(legislationSearchStatus.value).toBe("404");
    expect(legislationSearchError.value?.message).toBe(
      "No results found for test-id",
    );
  });
  it("throws an error when the ids don't macht", async () => {
    dataRef.value = {
      member: [{ item: { abbreviation: "test-id" } }],
    };
    const { legislationSearchError } = await getGermanOriginal("cde");
    expect(legislationSearchError.value?.message).toBe(
      "The fetched legislation does not match the requested ID: cde",
    );
  });
});
