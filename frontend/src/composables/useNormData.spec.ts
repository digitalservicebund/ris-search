import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, expect, it, vi } from "vitest";
import { useFetchNormArticleContent, useFetchNormContent } from "./useNormData";
import type {
  LegislationExpression,
  LegislationManifestation,
  LegislationWork,
} from "~/types";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

mockNuxtImport("useNuxtApp", () => {
  const nuxtApp = (globalThis as unknown as Window)?.useNuxtApp?.() ?? {};
  return () => {
    return {
      ...nuxtApp,
      $risBackend: mockFetch,
    };
  };
});

describe("useNormData", () => {
  const consoleInfoMock = vi
    .spyOn(console, "info")
    .mockImplementation(() => undefined);
  const consoleErrorMock = vi
    .spyOn(console, "error")
    .mockImplementation(() => undefined);

  afterAll(() => {
    consoleInfoMock.mockReset();
    consoleErrorMock.mockReset();
  });
  beforeEach(() => {
    mockFetch.mockReset();
    // Needed because useAsyncData caches its result for the same keys
    clearNuxtData();
  });

  const expressionEli = "test-eli";
  const mockMetadata = {
    workExample: {
      encoding: [
        {
          "@id": "test-encoding-id",
          encodingFormat: "text/html",
          contentUrl: "/v1/test-content-url.html",
          "@type": "LegislationObject",
          inLanguage: "deu",
        },
      ],
      hasPart: [],
    } as Partial<LegislationExpression>,
  } as LegislationWork;
  const mockHtml = `
    <section class="dokumentenkopf">
      <div class="fussnoten">Footnote content</div>
      <span />
      <div class="titel">Title</div>
      <div class="akn-container"><p>Besonderer Hinweis</p></div>
      <ul class="nichtamtliche-fussnoten"><li>Notes</li></ul>
    </section>
    <section class="akn-proprietary">
      <dl>
        <dt>Vollzitat</dt>
          <dd class="ris-vollzitat">Vollzitat</dd>
        <dt>Standangabe</dt>
          <dd class="ris-standangabe" data-type="stand">Stand-Stand</dd>
          <dd class="ris-standangabe" data-type="hinweis">Stand-Hinweis 1;</dd>
          <dd class="ris-standangabe" data-type="hinweis">Stand-Hinweis 2</dd>
      </dl>
   </section>
   <div>Test HTML content</div>`;

  it("should fetch JSON and HTML data", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(mockHtml);

    const { data } = await useFetchNormContent(expressionEli);
    expect(data.value).toEqual({
      legislationWork: mockMetadata,
      html: mockHtml,
      htmlParts: {
        officialToc: undefined,
        heading: `<div class="titel">Title</div>`,
        headingAuthorialNotes: `<div class="fussnoten">Footnote content</div>`,
        headingAuthorialNotesLength: 16,
        headingNotes: `<ul class="nichtamtliche-fussnoten"><li>Notes</li></ul>`,
        prefaceContainer: `<div class="akn-container"><p>Besonderer Hinweis</p></div>`,
        standangaben: ["Stand-Stand"],
        standangabenHinweis: ["Stand-Hinweis 1;", "Stand-Hinweis 2"],
        vollzitat: "Vollzitat",
      },
    });

    expect(mockFetch).toHaveBeenCalledTimes(2);
    expect(mockFetch).toHaveBeenCalledWith("/v1/legislation/eli/test-eli");
    expect(mockFetch).toHaveBeenCalledWith("/v1/test-content-url.html", {
      headers: {
        Accept: "text/html",
      },
    });
  });

  it("should fetch JSON and HTML data for articles", async () => {
    const articleEId = "eid-1";
    const mockHtml = `<h2 class="einzelvorschrift">§ 1 Some article</h2><div>Test HTML content</div>`;

    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(mockHtml);

    const { data } = await useFetchNormArticleContent(
      expressionEli,
      articleEId,
    );
    expect(data.value).toEqual({
      legislationWork: mockMetadata,
      html: mockHtml,
      articleHeading: "§ 1 Some article",
    });

    expect(mockFetch).toHaveBeenCalledTimes(2);
    expect(mockFetch).toHaveBeenCalledWith("/v1/legislation/eli/test-eli");

    expect(mockFetch).toHaveBeenCalledWith("/v1/test-content-url/eid-1.html", {
      headers: {
        Accept: "text/html",
      },
    });
  });

  it("should throw an error if contentUrl is missing", async () => {
    const expressionEli = "test-eli";
    const mockMetadata = {
      workExample: {
        encoding: [
          {
            "@id": "test-encoding-id",
            encodingFormat: "application/json",
          } as Partial<LegislationManifestation>,
        ],
      },
    };

    mockFetch.mockReturnValueOnce(mockMetadata);

    const { error } = await useFetchNormContent(expressionEli);
    expect(error.value?.message).toEqual("contentUrl is missing");
    expect(mockFetch).toHaveBeenCalledTimes(1);
    expect(mockFetch).toHaveBeenCalledWith("/v1/legislation/eli/test-eli");
  });

  it.each([
    ["simple footnote", 15],
    ["<span>with html</span>", 9],
    ["\n   <span>With\n  whitespace  \n</span>   \n", 15],
  ])(
    "returns the text length for authorial note '%s'",
    async (footnote, expectedLength) => {
      mockFetch.mockReturnValueOnce(mockMetadata);
      mockFetch.mockReturnValueOnce(
        `<section class="dokumentenkopf"><div class="fussnoten">${footnote}</div></section>`,
      );

      const { data } = await useFetchNormContent(expressionEli);
      expect(data.value.htmlParts.headingAuthorialNotesLength).toBe(
        expectedLength,
      );
    },
  );

  it("inserts line breaks between consecutive bracketed blocks in footnotes", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(
      `<section class="dokumentenkopf"><ul class="nichtamtliche-fussnoten"><li class="fussnote"><p>(+++ Textnachweis ab: 1.1.2000 +++) (+++ Zur Anwendung vgl. § 5 +++)</p></li></ul></section>`,
    );

    const { data } = await useFetchNormContent(expressionEli);
    expect(data.value.htmlParts.headingNotes).toContain(
      "(+++ Textnachweis ab: 1.1.2000 +++)<br />(+++ Zur Anwendung vgl. § 5 +++)",
    );
  });
});
