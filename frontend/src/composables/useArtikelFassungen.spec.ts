import type { FetchHook } from "ofetch";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { LegislationExpression } from "~/types/api";
import { useArtikelFassungen } from "./useArtikelFassungen.ts";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

const contentUrl = "/v1/test-content-url.html";
const revision = "test-revision";
const gesamtausgabe = {
  "@id": "/v1/legislation/eli/test",
} as LegislationExpression;

function mockBackend({
  html = "<html><body><div>Content</div></body></html>",
  gesamtausgaben = { member: [gesamtausgabe] } as unknown,
  htmlFails = false,
  gesamtausgabenFail = false,
} = {}) {
  mockFetch.mockImplementation(async (url: string) => {
    if (url.endsWith("/legislations")) {
      if (gesamtausgabenFail) throw new Error("request failed");
      return gesamtausgaben;
    }
    if (htmlFails) throw new Error("request failed");
    return html;
  });
}

describe("useArtikelFassungen", () => {
  beforeEach(() => {
    mockFetch.mockReset();
  });

  it("fetches and stores the HTML body and Gesamtausgaben of a Fassung", async () => {
    mockBackend();

    const { fassungenCache, updateCache } = useArtikelFassungen();
    await updateCache({ key: "row-1", contentUrl, revision });

    expect(mockFetch).toHaveBeenCalledWith(contentUrl, {
      headers: { Accept: "text/html" },
    });
    expect(mockFetch).toHaveBeenCalledWith(
      `/v1/article/${revision}/legislations`,
    );
    expect(fassungenCache.value.get("row-1")).toEqual({
      html: "<div>Content</div>",
      gesamtausgaben: [gesamtausgabe],
      error: false,
    });
  });

  it("stores an empty Gesamtausgaben list when the response has no members", async () => {
    mockBackend({ gesamtausgaben: {} });

    const { fassungenCache, updateCache } = useArtikelFassungen();
    await updateCache({ key: "row-1", contentUrl, revision });

    expect(fassungenCache.value.get("row-1")).toEqual({
      html: "<div>Content</div>",
      gesamtausgaben: [],
      error: false,
    });
  });

  it("does not fetch again once the Fassung is cached", async () => {
    mockBackend();

    const { updateCache } = useArtikelFassungen();
    await updateCache({ key: "row-1", contentUrl, revision });
    await updateCache({ key: "row-1", contentUrl, revision });

    expect(mockFetch).toHaveBeenCalledTimes(2);
  });

  it.each([
    { case: "content URL", contentUrl: undefined, revision },
    { case: "revision", contentUrl, revision: undefined },
  ])(
    "stores an error entry without fetching when the $case is missing",
    async (params) => {
      const { fassungenCache, updateCache } = useArtikelFassungen();
      await updateCache({ key: "row-1", ...params });

      expect(mockFetch).not.toHaveBeenCalled();
      expect(fassungenCache.value.get("row-1")).toEqual({
        error: true,
        gesamtausgaben: [],
      });
    },
  );

  it.each([
    { case: "HTML request", htmlFails: true },
    { case: "Gesamtausgaben request", gesamtausgabenFail: true },
  ])("stores an error entry when the $case fails", async (options) => {
    mockBackend(options);

    const { fassungenCache, updateCache } = useArtikelFassungen();
    await updateCache({ key: "row-1", contentUrl, revision });

    expect(fassungenCache.value.get("row-1")).toEqual({
      error: true,
      gesamtausgaben: [],
    });
  });

  it("retries the requests after a previous failure", async () => {
    mockBackend({ htmlFails: true });

    const { fassungenCache, updateCache } = useArtikelFassungen();
    await updateCache({ key: "row-1", contentUrl, revision });

    mockBackend();
    await updateCache({ key: "row-1", contentUrl, revision });

    expect(mockFetch).toHaveBeenCalledTimes(4);
    expect(fassungenCache.value.get("row-1")).toEqual({
      html: "<div>Content</div>",
      gesamtausgaben: [gesamtausgabe],
      error: false,
    });
  });

  it("fetches and caches Fassungen independently per key", async () => {
    mockFetch.mockImplementation(async (url: string) =>
      url.endsWith("/legislations")
        ? { member: [] }
        : `<html><body>${url}</body></html>`,
    );

    const { fassungenCache, updateCache } = useArtikelFassungen();
    await updateCache({ key: "row-1", contentUrl: "/v1/row-1.html", revision });
    await updateCache({ key: "row-2", contentUrl: "/v1/row-2.html", revision });

    expect(fassungenCache.value.get("row-1")?.html).toBe("/v1/row-1.html");
    expect(fassungenCache.value.get("row-2")?.html).toBe("/v1/row-2.html");
    expect(mockFetch).toHaveBeenCalledTimes(4);
  });
});
