import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { expect, vi } from "vitest";
import { ref } from "vue";
import { useRisBackend } from "~/composables/useRisBackend";
import { useLegislationSearchForAbbreviation } from "~/composables/useSearchData";

const { useRisBackendMock, _executeMock } = vi.hoisted(() => {
  const _executeMock = vi.fn();

  return {
    useRisBackendMock: vi.fn(
      (_url: Ref<string>, _opts: Record<string, Ref<string>>) => ({
        status: ref("success"),
        data: ref({
          member: [
            { item: { abbreviation: "test-id" } },
            { item: { abbreviation: "abc" } },
          ],
        }),
        error: ref(null),
        pending: ref(false),
        execute: _executeMock,
        refresh: vi.fn(),
        clear: vi.fn(),
      }),
    ),
    _executeMock,
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

describe("useLegislationSearchForAbbreviation", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.setSystemTime(new Date("2025-10-13T00:00:00.000Z"));
  });

  beforeEach(() => {
    clearNuxtData();
  });

  afterAll(() => {
    vi.useRealTimers();
  });

  it("returns the correct data when the ids match", async () => {
    const { legislation, legislationSearchError, legislationSearchStatus } =
      await useLegislationSearchForAbbreviation("test-id");
    expect(useRisBackend).toHaveBeenCalledWith(
      "/v1/legislation?searchTerm=test-id&temporalCoverageFrom=2025-10-13&temporalCoverageTo=2025-10-13&size=100&pageIndex=0",
    );
    expect(unref(unref(legislation))).toEqual({
      item: { abbreviation: "test-id" },
    });
    expect(unref(unref(legislationSearchError))).toBeNull();
    expect(unref(unref(legislationSearchStatus))).toBe("success");
  });

  it("throws an error when the ids don't match", async () => {
    const { legislationSearchError } =
      await useLegislationSearchForAbbreviation("cde");
    expect(legislationSearchError?.value?.message).toBe(
      "The fetched legislation does not match the requested ID: cde",
    );
  });
});
