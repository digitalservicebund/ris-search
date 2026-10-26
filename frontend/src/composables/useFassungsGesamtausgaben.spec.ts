import type { FetchHook } from "ofetch";
import { beforeEach, describe, expect, it, vi } from "vitest";
import type { LegislationExpression } from "~/types/api";
import { useFassungsGesamtausgaben } from "./useFassungsGesamtausgaben";

const { mockFetch } = vi.hoisted(() => ({ mockFetch: vi.fn() }));

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

const gesamtausgaben = [
  { legislationIdentifier: "eli/bund/bgbl-1/2020/s1/2022-01-01/1/deu" },
  { legislationIdentifier: "eli/bund/bgbl-1/2020/s1/2023-01-01/1/deu" },
] as LegislationExpression[];

beforeEach(() => {
  mockFetch.mockReset();
  mockFetch.mockResolvedValue({ member: gesamtausgaben });
  // useAsyncData caches results per key across calls
  clearNuxtData();
});

describe("useFassungsGesamtausgaben", () => {
  it("fetches the Gesamtausgaben the Fassung is part of", async () => {
    const { data } = await useFassungsGesamtausgaben("revision-1");

    expect(mockFetch).toHaveBeenCalledWith(
      "/v1/article/revision-1/legislations",
    );
    expect(data.value).toEqual(gesamtausgaben);
  });

  it("returns an empty list without fetching if there is no revision", async () => {
    const { data } = await useFassungsGesamtausgaben(undefined);

    expect(data.value).toEqual([]);
    expect(mockFetch).not.toHaveBeenCalled();
  });

  it("fetches again when the revision changes", async () => {
    const revision = ref<string>();
    const { data } = await useFassungsGesamtausgaben(revision);

    revision.value = "revision-1";

    await vi.waitFor(() => expect(data.value).toEqual(gesamtausgaben));
  });

  it("returns an empty list if the response has no members", async () => {
    mockFetch.mockResolvedValue({});

    const { data } = await useFassungsGesamtausgaben("revision-1");

    expect(data.value).toEqual([]);
  });

  it("returns an empty list if the request fails", async () => {
    mockFetch.mockRejectedValue(new Error("Not Found"));

    const { data, error } = await useFassungsGesamtausgaben("revision-1");

    expect(error.value).toBeDefined();
    expect(data.value).toEqual([]);
  });
});
