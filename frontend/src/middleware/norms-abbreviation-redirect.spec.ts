import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import type { RouteLocationNormalizedGeneric } from "vue-router";
import type { LegislationExpression, SearchResult } from "~/types/api";
import normAbbreviationRedirect from "./norms-abbreviation-redirect";

const { useRisBackendMock } = vi.hoisted(() => ({
  useRisBackendMock: vi.fn(),
}));

mockNuxtImport("useRisBackend", () => useRisBackendMock);
mockNuxtImport("getCurrentDateInGermanyFormatted", () => () => "2024-01-01");

function makeRoute(abbreviation: string): RouteLocationNormalizedGeneric {
  return {
    params: { abbreviation },
  } as unknown as RouteLocationNormalizedGeneric;
}

const emptyRoute = {} as unknown as RouteLocationNormalizedGeneric;

function makeBackendResult(
  members: SearchResult<Pick<LegislationExpression, "legislationIdentifier">>[],
  error = null,
) {
  return {
    data: ref({ member: members }),
    error: ref(error),
  };
}

describe("normsAbbreviationRedirect middleware", () => {
  it("calls the backend with the abbreviation and current date", async () => {
    useRisBackendMock.mockReturnValue(makeBackendResult([]));

    await Promise.resolve(
      normAbbreviationRedirect(makeRoute("BGB"), emptyRoute),
    ).catch(() => {});

    expect(useRisBackendMock).toHaveBeenCalledWith("/v1/legislation", {
      query: { abbreviation: "BGB", mostRelevantOn: "2024-01-01" },
    });
  });

  it("returns redirect url if exactly one expression was found for the abbreviation", async () => {
    useRisBackendMock.mockReturnValue(
      makeBackendResult([
        {
          item: { legislationIdentifier: "eliIdentifier" },
          textMatches: [],
        },
      ]),
    );

    const result = await normAbbreviationRedirect(makeRoute("BGB"), emptyRoute);

    expect(result).toBe("/norms/eliIdentifier");
  });

  it("throws 404 when no expression is found for the abbreviation", async () => {
    useRisBackendMock.mockReturnValue(makeBackendResult([]));

    await expect(
      normAbbreviationRedirect(makeRoute("BGB"), emptyRoute),
    ).rejects.toMatchObject({ status: 404 });
  });

  it("throws 404 when multiple expressions are found for the abbreviation", async () => {
    useRisBackendMock.mockReturnValue(
      makeBackendResult([
        {
          item: { legislationIdentifier: "eli/bund/bgb/1896/regelungstext-1" },
          textMatches: [],
        },
        {
          item: { legislationIdentifier: "eli/bund/bgb/2000/regelungstext-1" },
          textMatches: [],
        },
      ]),
    );

    await expect(
      normAbbreviationRedirect(makeRoute("BGB"), emptyRoute),
    ).rejects.toMatchObject({ status: 404 });
  });

  it("throws the backend error when the backend request fails", async () => {
    useRisBackendMock.mockReturnValue({
      data: ref(null),
      error: ref({ status: 503 }),
    });

    await expect(
      normAbbreviationRedirect(makeRoute("BGB"), emptyRoute),
    ).rejects.toMatchObject({ status: 503 });
  });
});
