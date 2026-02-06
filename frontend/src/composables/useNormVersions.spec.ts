import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { useNormVersions } from "./useNormVersions";
import type { LegislationExpression, SearchResult } from "~/types";

const dummyData = {
  member: [
    { temporalCoverage: "2023-12-01/2300-10-01" },
    { temporalCoverage: "2021-12-01/2023-11-30" },
  ],
} as unknown as SearchResult<LegislationExpression>[];

const { useRisBackendMock } = vi.hoisted(() => {
  return {
    useRisBackendMock: vi.fn(() => {
      return {
        status: ref("success"),
        data: computed(() => dummyData),
        error: ref(null),
      };
    }),
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

beforeEach(() => {
  vi.mocked(useRisBackendMock).mockReset();
});

describe("useNormVersions", () => {
  it("returns a sorted list when there is no error", () => {
    const { sortedVersions } = useNormVersions("dummy-eli");
    expect(useRisBackendMock).toBeCalledWith(
      "/v1/legislation/work-example/dummy-eli",
      {
        immediate: true,
      },
    );
    expect(sortedVersions.value.length).toBe(2);
    expect(sortedVersions.value[0]?.temporalCoverage).toBe(
      "2023-12-01/2300-10-01",
    );
    expect(sortedVersions.value[1]?.temporalCoverage).toBe(
      "2021-12-01/2023-11-30",
    );
  });

  it("returns an empty list when an error occurs", () => {
    vi.mocked(useRisBackendMock).mockReturnValue({
      status: ref("error"),
      data: computed(() => undefined),
      error: ref("Error occurred"),
    } as unknown as ReturnType<typeof useRisBackendMock>);
    const { sortedVersions } = useNormVersions("dummy-eli");
    expect(sortedVersions.value).toEqual([]);
  });
});
