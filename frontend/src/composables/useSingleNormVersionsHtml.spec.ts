import type { FetchHook } from "ofetch";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useSingleNormVersionsHtml } from "./useSingleNormVersionsHtml";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

describe("useSingleNormVersionsHtml", () => {
  beforeEach(() => {
    mockFetch.mockReset();
  });

  const rowContentUrl = "/v1/test-content-url.html";

  it("fetches and stores the HTML body of a row", async () => {
    mockFetch.mockResolvedValueOnce(
      "<html><body><div>Content</div></body></html>",
    );

    const { rowsHtml, updateRowsHtml } = useSingleNormVersionsHtml();
    await updateRowsHtml("row-1", rowContentUrl);

    expect(mockFetch).toHaveBeenCalledWith(rowContentUrl, {
      headers: {
        Accept: "text/html",
      },
    });
    expect(rowsHtml.value.get("row-1")).toEqual({
      html: "<div>Content</div>",
      error: false,
    });
  });

  it("does not fetch again once the row's HTML is cached", async () => {
    mockFetch.mockResolvedValueOnce("<html><body>Content</body></html>");

    const { updateRowsHtml } = useSingleNormVersionsHtml();
    await updateRowsHtml("row-1", rowContentUrl);
    await updateRowsHtml("row-1", rowContentUrl);

    expect(mockFetch).toHaveBeenCalledTimes(1);
  });

  it("stores an error entry when there is no content URL", async () => {
    const { rowsHtml, updateRowsHtml } = useSingleNormVersionsHtml();
    await updateRowsHtml("row-1", undefined);

    expect(mockFetch).not.toHaveBeenCalled();
    expect(rowsHtml.value.get("row-1")).toEqual({ error: true });
  });

  it("stores an error entry when the request fails", async () => {
    mockFetch.mockRejectedValueOnce(new Error("request failed"));

    const { rowsHtml, updateRowsHtml } = useSingleNormVersionsHtml();
    await updateRowsHtml("row-1", rowContentUrl);

    expect(rowsHtml.value.get("row-1")).toEqual({ error: true });
  });

  it("retries the request after a previous failure", async () => {
    mockFetch.mockRejectedValueOnce(new Error("request failed"));
    mockFetch.mockResolvedValueOnce("<html><body>Content</body></html>");

    const { rowsHtml, updateRowsHtml } = useSingleNormVersionsHtml();
    await updateRowsHtml("row-1", rowContentUrl);
    await updateRowsHtml("row-1", rowContentUrl);

    expect(mockFetch).toHaveBeenCalledTimes(2);
    expect(rowsHtml.value.get("row-1")).toEqual({
      html: "Content",
      error: false,
    });
  });

  it("fetches and caches HTML independently per row", async () => {
    mockFetch.mockResolvedValueOnce("<html><body>Row 1</body></html>");
    mockFetch.mockResolvedValueOnce("<html><body>Row 2</body></html>");

    const { rowsHtml, updateRowsHtml } = useSingleNormVersionsHtml();
    await updateRowsHtml("row-1", "/v1/row-1.html");
    await updateRowsHtml("row-2", "/v1/row-2.html");

    expect(rowsHtml.value.get("row-1")?.html).toBe("Row 1");
    expect(rowsHtml.value.get("row-2")?.html).toBe("Row 2");
    expect(mockFetch).toHaveBeenCalledTimes(2);
  });
});
