import type { FetchHook } from "ofetch";
import { describe, expect, it, vi } from "vitest";
import { useDocumentHtml } from "./useDocumentHtml";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

function page(body: string) {
  return `<html><body>${body}</body></html>`;
}

describe("useDocumentHtml", () => {
  const consoleErrorMock = vi
    .spyOn(console, "error")
    .mockImplementation(() => undefined);

  afterAll(() => {
    consoleErrorMock.mockReset();
  });

  beforeEach(() => {
    mockFetch.mockReset();
    // Needed because useAsyncData caches its result for the same keys
    clearNuxtData();
  });

  it("fetches the HTML version of the document", async () => {
    mockFetch.mockResolvedValueOnce(page("<p>Text</p>"));

    await useDocumentHtml("/v1/rechtsprechung/ABC.html");

    expect(mockFetch).toHaveBeenCalledTimes(1);
    expect(mockFetch).toHaveBeenCalledWith("/v1/rechtsprechung/ABC.html", {
      headers: { Accept: "text/html" },
    });
  });

  it("returns the body of the document", async () => {
    const body = '<section id="a"><h2>Tenor</h2><p>Text</p></section>';
    mockFetch.mockResolvedValueOnce(page(body));

    const { data } = await useDocumentHtml("/v1/literature/ABC.html");

    expect(data.value?.body).toBe(body);
  });

  it("lists the sections with their id and title", async () => {
    mockFetch.mockResolvedValueOnce(
      page(
        '<section id="a"><h2>Tenor</h2></section><section id="b"><h2>Gründe</h2></section><section id="c"><p>No title</p></section>',
      ),
    );

    const { data } = await useDocumentHtml("/v1/literature/ABC.html");

    expect(data.value?.text.sections).toEqual([
      { id: "a", title: "Tenor" },
      { id: "b", title: "Gründe" },
      { id: "c", title: "" },
    ]);
  });

  it("keeps the sections in the body", async () => {
    mockFetch.mockResolvedValueOnce(
      page('<section id="a"><h2>Tenor</h2></section>'),
    );

    const { data } = await useDocumentHtml("/v1/literature/ABC.html");

    expect(data.value?.body).toContain('id="a"');
  });

  describe("first paragraph", () => {
    it("is the trimmed text of the first paragraph in a section", async () => {
      mockFetch.mockResolvedValueOnce(
        page(
          "<section><p>\n  Paragraph <b>text</b> here.  </p><p>Second</p></section>",
        ),
      );

      const { data } = await useDocumentHtml("/v1/rechtsprechung/ABC.html");

      expect(data.value?.text.firstParagraph).toBe("Paragraph text here.");
    });

    it("ignores paragraphs outside of sections", async () => {
      mockFetch.mockResolvedValueOnce(
        page("<p>Outside</p><div>No section paragraph</div>"),
      );

      const { data } = await useDocumentHtml("/v1/rechtsprechung/ABC.html");

      expect(data.value?.text.firstParagraph).toBeUndefined();
    });

    it("skips paragraphs that are completely empty", async () => {
      mockFetch.mockResolvedValueOnce(
        page("<section><p></p><p>Second</p></section>"),
      );

      const { data } = await useDocumentHtml("/v1/rechtsprechung/ABC.html");

      expect(data.value?.text.firstParagraph).toBe("Second");
    });

    it("skips paragraphs that contain only whitespace", async () => {
      mockFetch.mockResolvedValueOnce(
        page("<section><p> </p><p>Second</p></section>"),
      );

      const { data } = await useDocumentHtml("/v1/rechtsprechung/ABC.html");

      expect(data.value?.text.firstParagraph).toBe("Second");
    });

    it("is missing if no paragraph has any text", async () => {
      mockFetch.mockResolvedValueOnce(
        page("<section><p> </p><p></p></section>"),
      );

      const { data } = await useDocumentHtml("/v1/rechtsprechung/ABC.html");

      expect(data.value?.text.firstParagraph).toBeUndefined();
    });
  });

  describe("emptiness", () => {
    it("is not empty with content", async () => {
      mockFetch.mockResolvedValueOnce(page("<p>Foo Bar</p>"));

      const { data } = await useDocumentHtml("/v1/literature/ABC.html");

      expect(data.value?.isEmpty).toBe(false);
    });

    it("is empty without content", async () => {
      mockFetch.mockResolvedValueOnce(page(""));

      const { data } = await useDocumentHtml("/v1/literature/ABC.html");

      expect(data.value?.isEmpty).toBe(true);
    });

    it("is empty with only a heading", async () => {
      mockFetch.mockResolvedValueOnce(page("<h1>Still Empty</h1>"));

      const { data } = await useDocumentHtml("/v1/literature/ABC.html");

      expect(data.value?.isEmpty).toBe(true);
    });

    it("is empty if the response is empty", async () => {
      mockFetch.mockResolvedValueOnce("");

      const { data } = await useDocumentHtml("/v1/literature/ABC.html");

      expect(data.value?.isEmpty).toBe(true);
      expect(data.value?.text.sections).toEqual([]);
    });
  });

  it("returns an error and no data if the request fails", async () => {
    mockFetch.mockRejectedValueOnce(
      Object.assign(new Error("Not found"), { statusCode: 404 }),
    );

    const { data, error } = await useDocumentHtml("/v1/literature/ABC.html");

    expect(data.value).toBeUndefined();
    expect(error.value?.statusCode).toBe(404);
  });
});
