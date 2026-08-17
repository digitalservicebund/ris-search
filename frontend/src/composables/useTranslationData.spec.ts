import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { FetchHook } from "ofetch";
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
  const dataRefMock = ref(null) as Ref<unknown>;

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
    dataRef: dataRefMock,
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

beforeEach(() => {
  vi.clearAllMocks();
});

describe("fetchTranslationList", () => {
  it("returns a list when there is no error", async () => {
    const mockTranslationResponse = [{ "@id": "Cde" }, { "@id": "AbC" }];
    dataRef.value = mockTranslationResponse;
    const { translations } = await fetchTranslationList();

    expect(useRisBackendMock).toHaveBeenCalledWith("/v1/translatedLegislation");
    expect(translations.value).toEqual(mockTranslationResponse);
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

    const mockHtmlResponse =
      "<html><body><h1>Mock HTML Content</h1></body></html>";
    const expectedBody = "<h1>Mock HTML Content</h1>";

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
    expect(data.value?.htmlBody).toEqual(expectedBody);
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

  it("returns the first legislation work when the abbreviation matches", async () => {
    const mockResult = { item: { abbreviation: "test-id" } };
    dataRef.value = { member: [mockResult] };

    expect(await getGermanOriginal("test-id")).toEqual(mockResult);
    expect(useRisBackendMock).toHaveBeenCalledWith(
      "/v1/legislation?searchTerm=test-id&temporalCoverageFrom=2025-10-13&temporalCoverageTo=2025-10-13&size=100&pageIndex=0",
    );
  });

  it("returns null when the API returns an empty member list", async () => {
    dataRef.value = { member: [] };

    expect(await getGermanOriginal("test-id")).toBeNull();
  });

  it("returns null when the API returns no data", async () => {
    dataRef.value = null;

    expect(await getGermanOriginal("test-id")).toBeNull();
  });

  it("returns null when the returned abbreviation does not match", async () => {
    dataRef.value = { member: [{ item: { abbreviation: "test-id" } }] };

    expect(await getGermanOriginal("cde")).toBeNull();
  });
});
