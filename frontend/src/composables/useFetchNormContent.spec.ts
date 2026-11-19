import type { FetchHook } from "ofetch";
import { describe, expect, it, vi } from "vitest";
import type {
  LegislationExpression,
  LegislationManifestation,
} from "~/types/api";
import {
  useFetchNormArticleContent,
  useFetchNormContent,
} from "./useFetchNormContent";

const { mockFetch } = vi.hoisted(() => {
  return {
    mockFetch: vi.fn(),
  };
});

vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: FetchHook[]) => cbs,
}));

function proprietary(standangaben: string) {
  return `<section class="akn-proprietary"><dl><dt>Standangabe</dt>${standangaben}</dl></section>`;
}

describe("useFetchNormContent", () => {
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
  } as Partial<LegislationExpression>;

  function mockHtml(body: string) {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce("<html><body>" + body + "</body></html>");
  }

  it("should fetch JSON and HTML data", async () => {
    const expectedHtml = `
    <section class="dokumentenkopf">
      <div class="fussnoten">Footnote content</div>
      <div class="titel">Title</div>
      <div class="akn-container"><p>Besonderer Hinweis</p></div>
      <ul class="nichtamtliche-fussnoten"><li>Notes</li></ul>
    </section>
   <div class="akn-body">Test HTML content</div>`;

    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(
      "<html><body>" + expectedHtml + "</body></html>",
    );

    const { data } = await useFetchNormContent(expressionEli);
    expect(data.value).toEqual({
      legislation: mockMetadata,
      htmlParts: {
        officialToc: undefined,
        heading: `<div class="titel">Title</div>`,
        headingAuthorialNotes: `<div class="fussnoten">Footnote content</div>`,
        headingAuthorialNotesLength: 16,
        headingNotes: `<ul class="nichtamtliche-fussnoten"><li>Notes</li></ul>`,
        prefaceContainer: `<div class="akn-container"><p>Besonderer Hinweis</p></div>`,
        body: expectedHtml,
      },
      hasEmptyBody: false,
    });

    expect(mockFetch).toHaveBeenCalledTimes(2);
    expect(mockFetch).toHaveBeenCalledWith("/v1/legislation/eli/test-eli");
    expect(mockFetch).toHaveBeenCalledWith("/v1/test-content-url.html", {
      headers: {
        Accept: "text/html",
      },
    });
  });

  describe("Standangaben", () => {
    it("counts every Standangabe that isn't a note as Stand", async () => {
      mockHtml(
        proprietary(`
          <dd class="ris-standangabe" data-type="stand">Zuletzt geändert durch Testanpassungen</dd>
          <dd class="ris-standangabe" data-type="neuf">Neugefasst durch Testdaten</dd>
          <dd class="ris-standangabe" data-type="hinweis">Hinweis Testdaten</dd>`),
      );

      const { data } = await useFetchNormContent(expressionEli);

      expect(data.value.htmlParts.standangaben).toEqual([
        "Zuletzt geändert durch Testanpassungen",
        "Neugefasst durch Testdaten",
      ]);
      expect(data.value.htmlParts.standangabenHinweis).toEqual([
        "Hinweis Testdaten",
      ]);
    });

    it("ignores Standangaben outside of the proprietary metadata", async () => {
      mockHtml(`
        <div class="akn-body">
          <dd class="ris-standangabe" data-type="stand">Stand</dd>
          <dd class="ris-standangabe" data-type="hinweis">Hinweis</dd>
        </div>`);

      const { data } = await useFetchNormContent(expressionEli);

      expect(data.value.htmlParts.standangaben).toEqual([]);
      expect(data.value.htmlParts.standangabenHinweis).toEqual([]);
    });

    it("has no Standangaben if the norm has none", async () => {
      mockHtml(proprietary(""));

      const { data } = await useFetchNormContent(expressionEli);

      expect(data.value.htmlParts.standangaben).toEqual([]);
      expect(data.value.htmlParts.standangabenHinweis).toEqual([]);
    });
  });

  it("has no parts if the norm doesn't have them", async () => {
    mockHtml(`<div class="akn-act"><div class="akn-body">Content</div></div>`);

    const { data } = await useFetchNormContent(expressionEli);

    expect(data.value.htmlParts).toMatchObject({
      heading: undefined,
      headingAuthorialNotes: undefined,
      headingAuthorialNotesLength: undefined,
      headingNotes: undefined,
      officialToc: undefined,
      prefaceContainer: undefined,
    });
  });

  it("gets the preface from the document header", async () => {
    // Output of the backend XSLT for a preface container, see
    // backend/src/test/resources/data/XsltTransformerServiceTest/container.html
    const container = `<div class="akn-container" id="einleitung-1_dokcontainer-1">
      <p id="einleitung-1_dokcontainer-1_text-1">Diese Verordnung dient der Umsetzung der Richtlinie 01/01/EG</p>
    </div>`;
    mockHtml(`<div class="akn-act">
  <section class="dokumentenkopf" id="einleitung-1">
    ${container}
  </section>
</div>`);

    const { data } = await useFetchNormContent(expressionEli);

    expect(data.value.htmlParts.prefaceContainer).toBe(container);
  });

  it("only uses footnotes from the document header for the heading", async () => {
    mockHtml(`
      <section class="dokumentenkopf"><h1 class="titel">Title</h1></section>
      <div class="akn-body">
        <div class="fussnoten">Footnote in the body</div>
        <ul class="nichtamtliche-fussnoten"><li>Note in the body</li></ul>
      </div>`);

    const { data } = await useFetchNormContent(expressionEli);

    expect(data.value.htmlParts.headingAuthorialNotes).toBeUndefined();
    expect(data.value.htmlParts.headingAuthorialNotesLength).toBeUndefined();
    expect(data.value.htmlParts.headingNotes).toBeUndefined();
  });

  it("passes every link to the link rewriter", async () => {
    mockHtml(`
      <section class="dokumentenkopf">
        <ul class="nichtamtliche-fussnoten"><li><a href="/in/header">link</a></li></ul>
      </section>
      <div class="akn-body">
        <a href="#anchor">anchor</a>
        <a>no href</a>
        <a href="regelungstext-1/art-z1.html">article</a>
      </div>`);
    // useAsyncData keeps the handler of the first call for a key, so tests
    // with their own link rewriter use their own ELI
    const rewriteLink = vi.fn((href: string | null) => href);

    await useFetchNormContent("every-link-eli", { rewriteLink });

    expect(rewriteLink.mock.calls).toEqual([
      ["/in/header"],
      ["#anchor"],
      [null],
      ["regelungstext-1/art-z1.html"],
    ]);
  });

  it("rewrites links in the parts taken from the document header", async () => {
    mockHtml(`
      <section class="dokumentenkopf">
        <ul class="nichtamtliche-fussnoten"><li><a href="/in/header">link</a></li></ul>
      </section>`);

    const { data } = await useFetchNormContent("header-link-eli", {
      rewriteLink: (href) => (href ? `/rewritten${href}` : null),
    });

    expect(data.value.htmlParts.headingNotes).toContain(
      'href="/rewritten/in/header"',
    );
  });

  it("removes the href if the link rewriter returns nothing", async () => {
    mockHtml(`<div class="akn-body"><a href="/somewhere">link</a></div>`);

    const { data } = await useFetchNormContent("removed-link-eli", {
      rewriteLink: () => null,
    });

    expect(data.value.htmlParts.body).toContain("<a>link</a>");
  });

  describe("article", () => {
    const articleEId = "eid-1";

    it("should fetch JSON and HTML data for articles", async () => {
      mockHtml(
        `<h2 class="einzelvorschrift">§ 1 Some article</h2><div>Test HTML content</div>`,
      );

      const { data } = await useFetchNormArticleContent(
        expressionEli,
        articleEId,
      );
      expect(data.value?.legislation).toEqual(mockMetadata);
      expect(data.value?.html.parts.heading).toBe("§ 1 Some article");
      expect(data.value?.html.text.headline).toBe("§ 1 Some article");
      expect(data.value?.html.body).toContain("<div>Test HTML content</div>");

      expect(mockFetch).toHaveBeenCalledTimes(2);
      expect(mockFetch).toHaveBeenCalledWith("/v1/legislation/eli/test-eli");

      expect(mockFetch).toHaveBeenCalledWith(
        "/v1/test-content-url/eid-1.html",
        {
          headers: {
            Accept: "text/html",
          },
        },
      );
    });

    it("takes the heading out of the body", async () => {
      mockHtml(
        `<h2 class="einzelvorschrift">§ 1 Some article</h2><div>Test HTML content</div>`,
      );

      const { data } = await useFetchNormArticleContent(
        expressionEli,
        articleEId,
      );

      expect(data.value?.html.body).toBe("<div>Test HTML content</div>");
    });

    it("keeps the content of the heading, but not the heading element", async () => {
      mockHtml(
        `<h2 class="einzelvorschrift"><span class="akn-num">§ 1</span> <b>Some</b> article</h2><div>Text</div>`,
      );

      const { data } = await useFetchNormArticleContent(
        expressionEli,
        articleEId,
      );

      expect(data.value?.html.parts.heading).toBe(
        '<span class="akn-num">§ 1</span> <b>Some</b> article',
      );
      expect(data.value?.html.text.headline).toBe("§ 1 Some article");
    });

    it("has no heading if the article doesn't have one", async () => {
      mockHtml(`<div>Test HTML content</div>`);

      const { data } = await useFetchNormArticleContent(
        expressionEli,
        articleEId,
      );

      expect(data.value?.html.parts.heading).toBeUndefined();
      expect(data.value?.html.text.headline).toBeUndefined();
      expect(data.value?.html.body).toBe("<div>Test HTML content</div>");
    });

    describe("first paragraph", () => {
      it.each([
        [
          "is the text of the first paragraph",
          "<p>Hello world</p><p>Second</p>",
          "Hello world",
        ],
        ["has no markup", "<p>Hello <b>world</b></p>", "Hello world"],
        ["skips paragraphs without text", "<p>   </p><p>Second</p>", "Second"],
        [
          "is missing if there is no paragraph",
          "<div>no paragraph here</div>",
          undefined,
        ],
        [
          "is missing if the only paragraph has just whitespace",
          "<p>   </p>",
          undefined,
        ],
      ])("%s", async (_, body, expected) => {
        mockHtml(body);

        const { data } = await useFetchNormArticleContent(
          expressionEli,
          articleEId,
        );

        expect(data.value?.html.text.firstParagraph).toBe(expected);
      });
    });
  });

  it("should throw an error if contentUrl is missing", async () => {
    const mockMetadataWithouContentUrl = {
      encoding: [
        {
          "@id": "test-encoding-id",
          encodingFormat: "application/json",
        } as Partial<LegislationManifestation>,
      ],
    };

    mockFetch.mockReturnValueOnce(mockMetadataWithouContentUrl);

    const { error } = await useFetchNormContent(expressionEli);
    expect(error.value?.message).toEqual("contentUrl is missing");
    expect(mockFetch).toHaveBeenCalledTimes(1);
    expect(mockFetch).toHaveBeenCalledWith("/v1/legislation/eli/test-eli");
  });

  it("does not return partial data when the HTML request fails", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockRejectedValueOnce(new Error("HTML request failed"));

    const { data, error } = await useFetchNormContent(expressionEli);

    expect(data.value).toBeUndefined();
    expect(error.value?.message).toBe("HTML request failed");
    expect(mockFetch).toHaveBeenCalledTimes(2);
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

  it("rewrites links", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(
      `<html><body><a href="/original/path">link</a></body></html>`,
    );

    const { data } = await useFetchNormContent("rewrite-test-eli", {
      rewriteLink: (href) => (href ? `/rewritten${href}` : null),
    });

    expect(data.value.htmlParts.body).toContain(
      `href="/rewritten/original/path"`,
    );
  });

  const preambleWithTocHtml = `
    <section class="eingangsformel" id="praeambel-n1">
      <section class="eingangsformel" id="praeambel-n1_formel-n1">
        <p id="praeambel-n1_formel-n1_text-n1">Der Bundestag hat …</p>
        <ul class="nichtamtliche-fussnoten">
          <li class="fussnote">
            <p id="meta-n1_editfnote-n1_text-n1">Eingangsformel: Eingangsformel Fußnote</p>
          </li>
        </ul>
      </section>
      <div class="inhaltsuebersicht" id="praeambel-n1_blockcontainer-n1">
        <span class="akn-heading">Inhaltsübersicht</span>
        <div class="official-toc">
          <div class="level-1"><span class="akn-span">Abschnitt 1</span></div>
        </div>
        <ul class="nichtamtliche-fussnoten">
          <li class="fussnote">
            <p id="meta-n1_editfnote-n2_text-n1">Inhaltsübersicht: präambel Fußnote</p>
          </li>
        </ul>
      </div>
    </section>`;

  it("adds official toc footnotes to the official toc", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(preambleWithTocHtml);

    const { data } = await useFetchNormContent(expressionEli);

    const parser = new DOMParser();
    const doc = parser.parseFromString(
      data.value.htmlParts.officialToc!,
      "text/html",
    );

    expect(doc.querySelector(".official-toc")).not.toBeNull();
    expect(doc.querySelector(".fussnote")).not.toBeNull();
    expect(
      doc.querySelector("#meta-n1_editfnote-n2_text-n1")?.textContent,
    ).toBe("Inhaltsübersicht: präambel Fußnote");
  });

  it("keeps Eingangsformel footnotes out of the official toc", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(preambleWithTocHtml);

    const { data } = await useFetchNormContent(expressionEli);

    expect(data.value.htmlParts.officialToc).not.toContain(
      "Eingangsformel: Eingangsformel Fußnote",
    );

    expect(data.value.htmlParts.body).toContain(
      "Eingangsformel: Eingangsformel Fußnote",
    );
  });

  it("sets hasEmptyBody to false if akn-body has content", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(
      `<html><body><div class="akn-act"><div class="akn-body">Content</div></div></body></html>`,
    );

    const { data } = await useFetchNormContent(expressionEli);
    expect(data.value.hasEmptyBody).toBe(false);
  });

  it("sets hasEmptyBody to true if akn-body div is empty", async () => {
    mockFetch.mockReturnValueOnce(mockMetadata);
    mockFetch.mockReturnValueOnce(
      `<html><body><div class="akn-act"><div class="akn-body"></div></div></body></html>`,
    );

    const { data } = await useFetchNormContent(expressionEli);
    expect(data.value.hasEmptyBody).toBe(true);
  });
});
