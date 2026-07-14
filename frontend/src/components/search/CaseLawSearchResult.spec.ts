import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import CaselawRecord from "~/components/search/CaselawSearchResult.vue";
import type { CaseLaw, SearchResult, TextMatch } from "~/types/api";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({
    fullPath: "/search?query=test&documentKind=R",
  })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

const searchResult: SearchResult<CaseLaw> = {
  item: {
    "@id": "",
    "@type": "Decision",
    deviatingDocumentNumber: [],
    ecli: "",
    encoding: [],
    inLanguage: "",
    keywords: [],
    documentNumber: "123",
    headline: "(Test Headline)",
    guidingPrinciple: "Guiding Principle",
    headnote: "Headnote",
    otherHeadnote: "Other Headnote",
    tenor: "Tenor",
    grounds: "Grounds",
    caseFacts: "Case Facts",
    decisionGrounds: "Decision Grounds",
    courtName: "Test Court",
    decisionDate: "2023-01-01",
    fileNumbers: ["123", "testing highlighted file number is here"],
    decisionName: ["Decision Name"],
    titleLine: "Title line",
    documentType: "Document Type",
  },
  textMatches: [],
};

function renderComponent({
  item = searchResult.item,
  textMatches = [],
}: Partial<SearchResult<CaseLaw>>) {
  const result: SearchResult<CaseLaw> = { item, textMatches };

  return render(CaselawRecord, {
    props: { searchResult: result, order: 0 },
    global: {
      stubs: {
        NuxtLink: {
          template:
            '<a :href="to.path ?? to" :data-from="to.query?.from"><slot /></a>',
          props: ["to"],
        },
      },
    },
  });
}

describe("CaselawSearchResult", () => {
  it("renders the expected title and secondary header row", () => {
    renderComponent({});
    expect(screen.getByRole("link")).toHaveTextContent("Test Headline");
    expect(screen.getByText("Decision Name")).toBeVisible();
  });

  it("has accessible description linking to result type", () => {
    renderComponent({});
    expect(
      screen.getByRole("link", {
        description: "Document Type",
      }),
    ).toHaveTextContent("Test Headline");
  });

  it("displays highlighted headline", () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "headline",
      text: `testing <mark>highlighted headline</mark> is here`,
      location: undefined,
    };

    const { container } = renderComponent({ textMatches: [textMatch] });

    const highlightedElements = container.querySelectorAll("mark");
    expect(highlightedElements).toHaveLength(1);
    expect(highlightedElements[0]).toHaveTextContent("highlighted headline");
  });

  it("displays highlighted file numbers", () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "fileNumbers",
      text: `testing <mark>highlighted file number</mark> is here`,
      location: undefined,
    };

    const { container } = renderComponent({ textMatches: [textMatch] });

    const highlightedElements = container.querySelectorAll("mark");
    expect(highlightedElements).toHaveLength(1);
    expect(highlightedElements[0]).toHaveTextContent("highlighted file number");

    expect(
      screen.getByText(
        (_, element) =>
          element?.textContent ===
          "123, testing highlighted file number is here",
      ),
    ).toBeInTheDocument();
  });

  it("displays highlighted text with correct class", () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "guidingPrinciple",
      text: "testing <mark>highlighted Text</mark> is here",
      location: undefined,
    };

    const { container } = renderComponent({ textMatches: [textMatch] });

    const highlightedElements = container.querySelectorAll("mark");
    expect(highlightedElements).toHaveLength(1);
    expect(highlightedElements[0]).toHaveTextContent("highlighted Text");
  });

  it("filters HTML tags except mark, i, b", () => {
    const text =
      '<mark>mark</mark> <i>i</i> <b>b</b> <img src="" alt="do not show"> <div>div</div> plain_text.';
    const expectedSanitized =
      "<mark>mark</mark> <i>i</i> <b>b</b>  div plain_text.";

    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "guidingPrinciple",
        text,
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "headline",
        text,
        location: undefined,
      },
    ];

    renderComponent({ textMatches });

    const heading = screen.getByRole("heading", { level: 2 });
    const headlineSpan = heading.querySelector("span:has(mark)");
    expect(headlineSpan?.innerHTML).toBe(expectedSanitized);

    const contentItems = screen.getAllByTestId("highlighted-field");
    expect(contentItems).toHaveLength(1);
    expect(contentItems[0]?.innerHTML).toBe(expectedSanitized);
  });

  it("uses item headline as fallback when no text match is present", () => {
    renderComponent({ textMatches: [] });

    expect(
      screen.getByRole("link", { name: "Test Headline" }),
    ).toBeInTheDocument();
  });

  it("displays static text when title is not present", () => {
    const searchResultWithoutTitle = {
      item: { ...searchResult.item, headline: "" },
      textMatches: [],
    };

    renderComponent(searchResultWithoutTitle);

    expect(screen.getByText("Titelzeile nicht vorhanden")).toBeInTheDocument();
  });

  it("uses Titlezeile when no decision name exists", () => {
    renderComponent({
      item: { ...searchResult.item, decisionName: [] },
    });

    expect(screen.getByText("Title line")).toBeVisible();
  });

  it("does not display a secondary header row without decision name or Titlezeile", () => {
    renderComponent({
      item: { ...searchResult.item, decisionName: [], titleLine: undefined },
    });

    expect(screen.queryByText("Decision Name")).not.toBeInTheDocument();
    expect(screen.queryByText("Title line")).not.toBeInTheDocument();
  });

  it("truncates the secondary header row to 90 characters", () => {
    const longDecisionName = "a".repeat(100);
    renderComponent({
      item: { ...searchResult.item, decisionName: [longDecisionName] },
    });

    expect(screen.getByText("a".repeat(90))).toBeVisible();
  });

  describe("fields when only one text match is returned", () => {
    const fields = [
      {
        field: "guidingPrinciple",
        value: "Leitsatz.",
        text: "<mark>Leitsatz</mark>.",
      },
      {
        field: "headnote",
        value: "Orientierungssatz.",
        text: "<mark>Orientierungssatz</mark>.",
      },
      {
        field: "otherHeadnote",
        value: "Sonstiger Orientierungssätze.",
        text: "<mark>Sonstiger Orientierungssätze</mark>.",
      },
      { field: "tenor", value: "Tenor.", text: "<mark>Tenor</mark>." },
      { field: "grounds", value: "Gründe.", text: "<mark>Gründe</mark>." },
      {
        field: "caseFacts",
        value: "Tatbestand.",
        text: "<mark>Tatbestand</mark>.",
      },
      {
        field: "decisionGrounds",
        value: "Entscheidungsgründe.",
        text: "<mark>Entscheidungsgründe</mark>.",
      },
    ];

    fields.forEach(({ field, value, text }) => {
      it(`displays '${value}'`, () => {
        const textMatches: TextMatch[] = [
          {
            name: field,
            text,
            "@type": "SearchResultMatch",
            location: undefined,
          },
        ];

        renderComponent({ textMatches });

        const contentItems = screen.getAllByTestId("highlighted-field");
        expect(contentItems).toHaveLength(1);
        expect(contentItems[0]).toHaveTextContent(value);
      });
    });
  });

  it("sorts fields by field map order", () => {
    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "caseFacts",
        text: "<mark>Tatbestand</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "guidingPrinciple",
        text: "<mark>Leitsatz</mark>.",
        location: undefined,
      },
    ];

    renderComponent({ textMatches });

    const contentItems = screen.getAllByTestId("highlighted-field");
    expect(contentItems).toHaveLength(2);
    expect(contentItems[0]).toHaveTextContent("Leitsatz.");
    expect(contentItems[1]).toHaveTextContent("Tatbestand.");
  });

  it("displays guiding principle first, if available, then up to 3 other matching items", () => {
    const guidingPrinciple: TextMatch = {
      "@type": "SearchResultMatch",
      name: "guidingPrinciple",
      text: "Guiding <mark>Principle</mark>.",
      location: undefined,
    };
    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "headnote",
        text: "<mark>Headnote</mark>.",
        location: undefined,
      },
      guidingPrinciple,
      {
        "@type": "SearchResultMatch",
        name: "otherHeadnote",
        text: "<mark>Other Headnote</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "tenor",
        text: "<mark>Tenor</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "grounds",
        text: "<mark>Grounds</mark>.",
        location: undefined,
      },
    ];

    renderComponent({ textMatches });

    const contentItems = screen.getAllByTestId("highlighted-field");

    expect(contentItems).toHaveLength(4);
    expect(contentItems[0]?.innerHTML).toBe(guidingPrinciple.text);
    expect(contentItems[1]?.innerHTML).toBe(textMatches[0]?.text);
    expect(contentItems[2]?.innerHTML).toBe(textMatches[2]?.text);
    expect(contentItems[3]?.innerHTML).toBe(textMatches[3]?.text);
  });

  it("returns an empty array when there are no caselaw fields", () => {
    const searchResultWithoutFields = {
      item: {
        ...searchResult.item,
        guidingPrinciple: "",
        headnote: "",
        otherHeadnote: "",
        tenor: "",
        grounds: "",
        caseFacts: "",
        decisionGrounds: "",
      },
      textMatches: [],
    };

    renderComponent(searchResultWithoutFields);

    const contentItems = screen.queryAllByTestId("highlighted-field");
    expect(contentItems).toHaveLength(0);
  });

  it("shows all returned text matches", () => {
    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "guidingPrinciple",
        text: "<mark>Guiding Principle</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "headnote",
        text: "<mark>Headnote</mark>.",
        location: undefined,
      },
    ] as TextMatch[];

    renderComponent({ textMatches });

    const contentItems = screen.getAllByTestId("highlighted-field");
    expect(contentItems).toHaveLength(2);
    expect(contentItems[0]).toHaveTextContent("Guiding Principle.");
    expect(contentItems[1]).toHaveTextContent("Headnote.");
  });

  it("shows up to 4 fields sorted by field map order", () => {
    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "grounds",
        text: "<mark>Grounds</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "headnote",
        text: "<mark>Headnote</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "guidingPrinciple",
        text: "<mark>Guiding Principle</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "otherHeadnote",
        text: "<mark>Other Headnote</mark>.",
        location: undefined,
      },
      {
        "@type": "SearchResultMatch",
        name: "tenor",
        text: "<mark>Tenor</mark>.",
        location: undefined,
      },
    ];

    renderComponent({ textMatches });

    const contentItems = screen.getAllByTestId("highlighted-field");

    expect(contentItems).toHaveLength(4);
    expect(contentItems[0]).toHaveTextContent("Guiding Principle.");
    expect(contentItems[1]).toHaveTextContent("Headnote.");
    expect(contentItems[2]).toHaveTextContent("Other Headnote.");
    expect(contentItems[3]).toHaveTextContent("Tenor.");
  });

  it("does not display a field when the text match has no highlight", () => {
    renderComponent({
      textMatches: [
        {
          "@type": "SearchResultMatch",
          name: "guidingPrinciple",
          text: "plain text without any highlight",
          location: undefined,
        },
      ],
    });

    expect(screen.queryAllByTestId("highlighted-field")).toHaveLength(0);
  });

  it("includes the current search URL as query param in the detail page link", () => {
    useRouteMock.mockReturnValue({
      fullPath: "/search?query=BGB&documentKind=R&pageIndex=2",
    });

    renderComponent({});

    const link = screen.getByRole("link", { name: "Test Headline" });
    expect(link).toHaveAttribute(
      "data-from",
      "/search?query=BGB&documentKind=R&pageIndex=2",
    );
  });

  it("includes the current search URL as query param in preview section links", () => {
    useRouteMock.mockReturnValue({
      fullPath: "/search?query=BGB&documentKind=R&pageIndex=2",
    });

    renderComponent({
      textMatches: [
        {
          "@type": "SearchResultMatch",
          name: "guidingPrinciple",
          text: "testing <mark>highlighted</mark> text",
          location: undefined,
        },
      ],
    });

    const sectionLink = screen.getByRole("link", { name: "Leitsatz:" });
    expect(sectionLink).toHaveAttribute(
      "data-from",
      "/search?query=BGB&documentKind=R&pageIndex=2",
    );
  });
});
