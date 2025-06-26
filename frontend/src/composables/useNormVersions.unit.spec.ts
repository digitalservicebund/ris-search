import { ref } from "vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useNormVersions } from "./useNormVersions";
import { useFetch } from "#app";
import type { LegislationWork, SearchResult } from "~/types";

const dummyData = {
  member: [
    { item: { workExample: { temporalCoverage: "2023-12-01/2300-10-01" } } },
    { item: { workExample: { temporalCoverage: "2021-12-01/2023-11-30" } } },
  ],
} as unknown as SearchResult<LegislationWork>[];

const statusTestData = {
  historical: {
    item: {
      workExample: {
        temporalCoverage: "2020-01-01/2022-12-31",
        legislationLegalForce: "NotInForce",
      },
    },
  } as unknown as SearchResult<LegislationWork>,
  inForce: {
    item: {
      workExample: {
        temporalCoverage: "2023-01-01/2923-12-31",
        legislationLegalForce: "InForce",
      },
    },
  } as unknown as SearchResult<LegislationWork>,
  future: {
    item: {
      workExample: {
        temporalCoverage: "2924-01-01/..",
        legislationLegalForce: "NotInForce",
      },
    },
  } as unknown as SearchResult<LegislationWork>,
};

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
      "2021-12-01/2023-11-30",
    );
    expect(sortedVersions.value[1].item.workExample.temporalCoverage).toBe(
      "2023-12-01/2300-10-01",
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

  Object.keys(statusTestData).forEach((status) => {
    it(`returns correct status for version with ${status} status`, () => {
      const version = statusTestData[status as keyof typeof statusTestData];
      expect(getVersionStatus(version.item)).toBe(status);
    });
  });
});
