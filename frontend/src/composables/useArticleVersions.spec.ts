import type { FetchHook } from "ofetch";
import { describe, expect, it, vi } from "vitest";
import type { ArticleVersion, JSONLDList } from "~/types/api";
import { useArticleVersions } from "./useArticleVersions.ts";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

describe("useArticleVersions", () => {
  beforeEach(() => {
    mockFetch.mockReset();
    // Needed because useAsyncData caches its result for the same keys
    clearNuxtData();
  });

  const expressionEli = "test-eli";
  const articleEId = "eid-1";

  it("fetches article versions for the given eli and eId", async () => {
    const articles = [{ "@id": "foo" }] as ArticleVersion[];
    mockFetch.mockResolvedValueOnce({
      member: articles,
    } as JSONLDList<ArticleVersion>);

    const { data } = await useArticleVersions(expressionEli, articleEId);

    expect(mockFetch).toHaveBeenCalledWith(
      `/v1/article/work-example/eli/${expressionEli}/${articleEId}`,
    );
    expect(data.value).toEqual(articles);
  });

  it("exposes an error without throwing when the request fails", async () => {
    mockFetch.mockRejectedValueOnce(new Error("request failed"));

    const { data, error } = await useArticleVersions(expressionEli, articleEId);

    expect(data.value).toBeUndefined();
    expect(error.value?.message).toBe("request failed");
  });
});
