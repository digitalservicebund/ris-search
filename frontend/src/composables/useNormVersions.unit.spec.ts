import { ref } from "vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useNormVersions } from "./useNormVersions";
import { useFetch } from "#app";
import type { LegislationWork, SearchResult } from "~/types";

const dummyData = {
  member: [
    { item: { workExample: { temporalCoverage: "2023" } } },
    { item: { workExample: { temporalCoverage: "2021" } } },
  ],
} as unknown as SearchResult<LegislationWork>[];

vi.mock("#app", () => {
  return {
    useFetch: vi.fn(),
  };
});

beforeEach(() => {
  vi.mocked(useFetch).mockReset();
});

describe("useNormVersions", () => {
  it("returns a sorted list when there is no error", () => {
    vi.mocked(useFetch).mockReturnValue({
      status: ref("success"),
      data: computed(() => dummyData),
      error: ref(null),
    } as unknown as ReturnType<typeof useFetch>);
    const { sortedVersions } = useNormVersions("dummy-eli");
    expect(sortedVersions.value.length).toBe(2);
    expect(sortedVersions.value[0].item.workExample.temporalCoverage).toBe(
      "2021",
    );
    expect(sortedVersions.value[1].item.workExample.temporalCoverage).toBe(
      "2023",
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
