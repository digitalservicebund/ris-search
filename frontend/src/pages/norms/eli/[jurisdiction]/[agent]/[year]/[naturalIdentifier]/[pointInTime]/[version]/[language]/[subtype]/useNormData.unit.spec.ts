 
import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { useFetchNormArticleContent, useFetchNormContent } from "./useNormData";
import type {
  LegislationExpression,
  LegislationManifestation,
  LegislationWork,
} from "@/types";

import { mockNuxtImport } from "@nuxt/test-utils/runtime";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

mockNuxtImport("useRequestFetch", () => {
  return () => mockFetch;
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
  afterEach(() => {
    mockFetch.mockReset();
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
      <div class="akn-notes">Notes</div>
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
        headingNotes: `<div class="akn-notes">Notes</div>`,
        prefaceContainer: `<div class="akn-container"><p>Besonderer Hinweis</p></div>`,
        standangaben: ["Stand-Stand"],
        standangabenHinweis: ["Stand-Hinweis 1;", "Stand-Hinweis 2"],
        vollzitat: "Vollzitat",
      },
    });

    expect(mockFetch).toHaveBeenCalledTimes(2);
    expect(mockFetch).toHaveBeenCalledWith("/api/v1/legislation/eli/test-eli");
    expect(mockFetch).toHaveBeenCalledWith("/api/v1/test-content-url.html", {
      // note the /api prefix
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
    expect(mockFetch).toHaveBeenCalledWith("/api/v1/legislation/eli/test-eli");

    expect(mockFetch).toHaveBeenCalledWith(
      "/api/v1/test-content-url/eid-1.html",
      {
        headers: {
          Accept: "text/html",
        },
      },
    );
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
    expect(mockFetch).toHaveBeenCalledWith("/api/v1/legislation/eli/test-eli");
  });
});
