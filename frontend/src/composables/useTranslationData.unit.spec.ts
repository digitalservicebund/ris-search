import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, beforeEach, vi } from "vitest";
import {
  fetchTranslationList,
  fetchTranslationAndHTML,
  getGermanOriginal,
} from "~/composables/useTranslationData";
import type { TranslationContent } from "~/composables/useTranslationData";

vi.mock("~/composables/useBackendURL", () => {
  return {
    useBackendURL: () => "https://mock-backend",
  };
});

const requestFetchMock = vi.fn();

mockNuxtImport("useRequestFetch", () => {
  return () => requestFetchMock;
});

beforeEach(() => {
  requestFetchMock.mockReset();
});

describe("fetchTranslationList", () => {
  it("returns a list when there is no error", async () => {
    const mockTranslationListData: TranslationContent[] = [
      {
        "@id": "Cde",
        name: "Act B",
        inLanguage: "en",
        translator: "…",
        translationOfWork: "Gesetz B",
        about: "…",
        "ris:filename": "englisch_cde.html",
      },
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

    requestFetchMock.mockResolvedValueOnce(mockTranslationListData);
    const { data, error } = await fetchTranslationList();

    expect(requestFetchMock).toHaveBeenCalledWith(
      "https://mock-backend/v1/translatedLegislation",
    );
    expect(error.value).toBeNull();
    expect(data.value).toHaveLength(2);
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

    requestFetchMock.mockResolvedValueOnce(mockTranslationResponse);
    requestFetchMock.mockResolvedValueOnce(mockHtmlResponse);

    const { data, error } = await fetchTranslationAndHTML("AbC");

    expect(requestFetchMock).toHaveBeenCalledWith(
      "https://mock-backend/v1/translatedLegislation?id=AbC",
    );

    expect(requestFetchMock).toHaveBeenCalledWith(
      "https://mock-backend/v1/translatedLegislation/englisch_abc.html",
      {
        headers: {
          Accept: "text/html",
        },
      },
    );

    expect(error.value).toBeNull();

    expect(data.value?.content).toEqual(mockTranslationResponse[0]);
    expect(data.value?.html).toEqual(mockHtmlResponse);
  });

  it("returns 404 when list is empty", async () => {
    const mockTranslationResponse: TranslationContent[] = [];
    requestFetchMock.mockResolvedValueOnce(mockTranslationResponse);

    const { data, error } = await fetchTranslationAndHTML("FgH");

    expect(requestFetchMock).toHaveBeenCalledWith(
      "https://mock-backend/v1/translatedLegislation?id=FgH",
    );

    expect(requestFetchMock).toHaveBeenCalledTimes(1);

    expect(error.value).not.toBeNull();
    expect(error.value?.statusCode).toBe(404);
    expect(error.value?.statusMessage).toBe("Translation not found");

    expect(data.value).toBeNull();
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
    requestFetchMock.mockResolvedValueOnce(mockTranslationResponse);

    const { data, error } = await fetchTranslationAndHTML("FgH");

    expect(requestFetchMock).toHaveBeenCalledWith(
      "https://mock-backend/v1/translatedLegislation?id=FgH",
    );

    expect(requestFetchMock).toHaveBeenCalledTimes(1);

    expect(error.value).not.toBeNull();
    expect(error.value?.statusCode).toBe(404);
    expect(error.value?.statusMessage).toBe("Translation filename not found");

    expect(data.value).toBeNull();
  });
});

describe("getGermanOriginal", () => {
  beforeAll(() => {
    vi.setSystemTime(new Date("2025-10-13T00:00:00.000Z"));
  });

  afterAll(() => {
    vi.useRealTimers();
  });

  it("returns first legislation work when API returns results", async () => {
    const mockResult = { id: "abc123" };
    requestFetchMock.mockResolvedValueOnce({ member: [mockResult] });

    const { data, error } = await getGermanOriginal("test-id");

    expect(data.value).toEqual(mockResult);
    expect(error.value).toBeNull();
    expect(requestFetchMock).toHaveBeenCalledWith(
      "https://mock-backend/v1/legislation?searchTerm=test-id&temporalCoverageFrom=2025-10-13&temporalCoverageTo=2025-10-13&size=100&pageIndex=0",
    );
  });

  it("returns 404 error when API returns empty member list", async () => {
    requestFetchMock.mockResolvedValueOnce({ member: [] });

    const { data, error } = await getGermanOriginal("test-id");

    expect(data.value).toBeNull();
    expect(error.value).not.toBeNull();
    expect(error.value?.statusCode).toBe(404);
    expect(error.value?.statusMessage).toBe("Not Found");
  });

  it("returns 404 error when API returns null", async () => {
    requestFetchMock.mockResolvedValueOnce(null);

    const { data, error } = await getGermanOriginal("test-id");

    expect(data.value).toBeNull();
    expect(error.value).not.toBeNull();
    expect(error.value?.statusCode).toBe(404);
    expect(error.value?.statusMessage).toBe("Not Found");
  });
});
