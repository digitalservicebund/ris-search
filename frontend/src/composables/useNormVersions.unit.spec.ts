import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { useNormVersions } from "./useNormVersions";
import { useFetch } from "#app";
import type { LegislationWork, SearchResult } from "~/types";

const dummyData = {
  member: [
    { item: { workExample: { temporalCoverage: "2023-12-01/2300-10-01" } } },
    { item: { workExample: { temporalCoverage: "2021-12-01/2023-11-30" } } },
  ],
} as unknown as SearchResult<LegislationWork>[];

const { useFetchMock } = vi.hoisted(() => {
  return {
    useFetchMock: vi.fn(() => {
      return {
        status: ref("success"),
        data: computed(() => dummyData),
        error: ref(null),
      };
    }),
  };
});

mockNuxtImport("useFetch", () => {
  return useFetchMock;
});

beforeEach(() => {
  vi.mocked(useFetch).mockReset();
});

describe("useNormVersions", () => {
  it("returns a sorted list when there is no error", () => {
    const { sortedVersions } = useNormVersions("dummy-eli");
    expect(useFetchMock).toBeCalledWith(
      "/v1/legislation",
      {
        immediate: true,
        params: {
          eli: "dummy-eli",
          sort: "-temporalCoverageFrom",
        },
      },
      expect.anything(),
    );
    expect(sortedVersions.value.length).toBe(2);
    expect(sortedVersions.value[0]?.item.workExample.temporalCoverage).toBe(
      "2023-12-01/2300-10-01",
    );
    expect(sortedVersions.value[1]?.item.workExample.temporalCoverage).toBe(
      "2021-12-01/2023-11-30",
    );
  });

  it("returns an empty list when an error occurs", () => {
    vi.mocked(useFetch).mockReturnValue({
      status: ref("error"),
      data: computed(() => undefined),
      error: ref("Error occurred"),
    } as unknown as ReturnType<typeof useFetch>);
    const { sortedVersions } = useNormVersions("dummy-eli");
    expect(sortedVersions.value).toEqual([]);
  });
});
