import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import CaselawRecord from "~/components/Search/Result/CaselawSearchResult.vue";
import type { CaseLaw, SearchResult, TextMatch } from "~/types";

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
    documentType: "Document Type",
  },
  textMatches: [],
};

function renderComponent({
  item = searchResult.item,
  textMatches = [],
}: Partial<SearchResult<CaseLaw>>) {
  const searchResult: SearchResult<CaseLaw> = { item, textMatches };

  return render(CaselawRecord, {
    props: { searchResult, order: 0 },
    global: {
      stubs: {
        NuxtLink: { template: '<a :href="to"><slot /></a>', props: ["to"] },
      },
    },
  });
}

describe("CaselawSearchResult.vue", () => {
  it("renders the expected title", () => {
    renderComponent({});
    expect(
      screen.getByRole("link", { name: "Decision Name — Test Headline" }),
    ).toBeInTheDocument();
  });

  it("displays highlighted headline", () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "headline",
      text: `testing <mark>highlighted headline</mark> is here`,
      location: null,
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
      location: null,
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
      location: null,
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
        location: null,
      },
      {
        "@type": "SearchResultMatch",
        name: "headline",
        text,
        location: null,
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

  it("displays static text when title is not present", () => {
    const searchResultWithoutTitle = {
      item: { ...searchResult.item, headline: "" },
      textMatches: [],
    };

    renderComponent(searchResultWithoutTitle);

    expect(screen.getByText("Titelzeile nicht vorhanden")).toBeInTheDocument();
  });

  describe("fields when only one text match is returned", () => {
    const fields = [
      { field: "guidingPrinciple", value: "Leitsatz." },
      { field: "headnote", value: "Orientierungssatz." },
      { field: "otherHeadnote", value: "Sonstiger Orientierungssätze." },
      { field: "tenor", value: "Tenor." },
      { field: "grounds", value: "Gründe." },
      { field: "caseFacts", value: "Tatbestand." },
      { field: "decisionGrounds", value: "Entscheidungsgründe." },
    ];

    fields.forEach(({ field, value }) => {
      it(`displays '${value}'`, () => {
        const textMatches: TextMatch[] = [
          {
            name: field,
            text: value,
            "@type": "SearchResultMatch",
            location: null,
          },
        ];

        renderComponent({ textMatches });

        const contentItems = screen.getAllByTestId("highlighted-field");
        expect(contentItems).toHaveLength(1);
        expect(contentItems[0]).toHaveTextContent(value);
      });
    });
  });

  it("displays the first field in order only when there are no highlights if there are multiple fields", () => {
    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "guidingPrinciple",
        text: "Leitsatz.",
        location: null,
      },
      {
        "@type": "SearchResultMatch",
        name: "caseFacts",
        text: "Tatbestand.",
        location: null,
      },
    ];

    renderComponent({ textMatches });

    const contentItems = screen.getAllByTestId("highlighted-field");
    expect(contentItems).toHaveLength(1);
    expect(contentItems[0]).toHaveTextContent("Leitsatz.");
  });

  it("displays guiding principle first, if available, then up to 3 other matching items", () => {
    const guidingPrinciple: TextMatch = {
      "@type": "SearchResultMatch",
      name: "guidingPrinciple",
      text: "Guiding <mark>Principle</mark>.",
      location: null,
    };
    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "headnote",
        text: "<mark>Headnote</mark>.",
        location: null,
      },
      guidingPrinciple,
      {
        "@type": "SearchResultMatch",
        name: "otherHeadnote",
        text: "<mark>Other Headnote</mark>.",
        location: null,
      },
      {
        "@type": "SearchResultMatch",
        name: "tenor",
        text: "<mark>Tenor</mark>.",
        location: null,
      },
      {
        "@type": "SearchResultMatch",
        name: "grounds",
        text: "<mark>Grounds</mark>.",
        location: null,
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

  it("shows only the first textMatch if none contain marks", () => {
    const searchResultWithoutHighlights = {
      item: searchResult.item,
      textMatches: [
        {
          "@type": "SearchResultMatch",
          name: "guidingPrinciple",
          text: "Guiding Principle.",
          location: null,
        },
        {
          "@type": "SearchResultMatch",
          name: "headnote",
          text: "This should not even be shown.",
          location: null,
        },
      ] as TextMatch[],
    };

    renderComponent(searchResultWithoutHighlights);

    const contentItems = screen.getAllByTestId("highlighted-field");
    expect(contentItems).toHaveLength(1);
    expect(contentItems[0]).toHaveTextContent("Guiding Principle.");
  });

  it("returns the first field from caselaw fields and up to three highlighted fields if highlights are present", () => {
    const textMatches: TextMatch[] = [
      {
        "@type": "SearchResultMatch",
        name: "guidingPrinciple",
        text: "Guiding Principle.",
        location: null,
      },
      {
        "@type": "SearchResultMatch",
        name: "otherHeadnote",
        text: "<mark>Other Headnote</mark>.",
        location: null,
      },
      {
        "@type": "SearchResultMatch",
        name: "tenor",
        text: "<mark>Tenor</mark>.",
        location: null,
      },
      {
        "@type": "SearchResultMatch",
        name: "grounds",
        text: "<mark>Grounds</mark>.",
        location: null,
      },
    ];

    renderComponent({ textMatches });

    const contentItems = screen.getAllByTestId("highlighted-field");

    expect(contentItems).toHaveLength(4);
    expect(contentItems[0]?.innerHTML).toBe("Guiding Principle.");
    expect(contentItems[1]?.innerHTML).toBe(textMatches[1]?.text);
    expect(contentItems[2]?.innerHTML).toBe(textMatches[2]?.text);
    expect(contentItems[3]?.innerHTML).toBe(textMatches[3]?.text);
  });
});
