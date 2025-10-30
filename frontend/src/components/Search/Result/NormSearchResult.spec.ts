import { render, screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import NormSearchResult from "./NormSearchResult.vue";
import type { LegislationWork, SearchResult, TextMatch } from "~/types";

const mockSearchResult: SearchResult<LegislationWork> = {
  item: {
    name: "Test Norm",
    abbreviation: "TN",
    legislationIdentifier: "eli/bund/bgbl-0/1999/ab/regelungstext-1",
    "@type": "Legislation",
    "@id": "eli/bund/bgbl-0/1999/ab/regelungstext-1",
    alternateName: "NoRM",
    legislationDate: "1999-12-14",
    datePublished: "1999-12-21",
    isPartOf: {
      name: "The Official Gazette",
    },
    workExample: {
      "@id": "eli/bund/bgbl-0/1999/abc/1999-12-31/1/deu/regelungstext-1",
      "@type": "Legislation",
      hasPart: [],
      legislationIdentifier:
        "eli/bund/bgbl-0/1999/abc/1999-12-31/1/deu/regelungstext-1",
      encoding: [],
      tableOfContents: [],
      legislationLegalForce: "InForce",
      temporalCoverage: "2000-01-01/..",
    },
  },
  textMatches: [
    {
      name: "name",
      text: "Highlighted <mark>Test Title</mark>",
      "@type": "SearchResultMatch",
      location: null,
    },
    {
      name: "Article 1",
      text: "Example Text 1",
      "@type": "SearchResultMatch",
      location: null,
    },
    {
      name: "Article 2",
      text: "Example Text 2",
      "@type": "SearchResultMatch",
      location: null,
    },
    {
      name: "Article 3",
      text: "Example Text 3",
      "@type": "SearchResultMatch",
      location: null,
    },
    {
      name: "Article 4",
      text: "Example Text 4",
      "@type": "SearchResultMatch",
      location: null,
    },
  ],
};

function renderComponent(
  searchResult: SearchResult<LegislationWork> = mockSearchResult,
  order: number = 0,
) {
  return render(NormSearchResult, {
    props: { searchResult, order },
    global: {
      stubs: {
        NuxtLink: { template: '<a :href="to"><slot /></a>', props: ["to"] },
      },
    },
  });
}

const mocks = vi.hoisted(() => {
  return {
    usePrivateFeaturesFlag: vi.fn().mockReturnValue(false),
  };
});

vi.mock("~/composables/usePrivateFeaturesFlag", () => {
  return { usePrivateFeaturesFlag: mocks.usePrivateFeaturesFlag };
});

describe("NormSearchResult.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.restoreAllMocks();
  });

  it("renders correctly with all props", () => {
    mocks.usePrivateFeaturesFlag.mockReturnValue(true);
    renderComponent();
    expect(screen.getByText("TN")).toBeInTheDocument();
    expect(screen.getByText(/Norm/)).toBeInTheDocument();
    expect(screen.getByText("01.01.2000")).toBeInTheDocument();
    expect(screen.queryByText("14.12.1999")).not.toBeInTheDocument();

    const heading = screen.getByRole("heading", { name: /Test Title/i });
    expect(heading.innerHTML).toBe("Highlighted <mark>Test Title</mark>");

    expect(screen.getByText("Article 1")).toBeInTheDocument();
    expect(screen.getByText(/Example Text 1/)).toBeInTheDocument();

    const highlightSection = screen.getByTestId("highlights");
    expect(highlightSection.children).toHaveLength(4);
  });

  it("renders ausfertigungs datum when in prototype environment", () => {
    renderComponent();
    expect(screen.getByText("14.12.1999")).toBeInTheDocument();
    expect(screen.queryByText("01.01.2000")).not.toBeInTheDocument();
  });

  it("uses item name when official title is not available", () => {
    const modifiedSearchResult = {
      ...mockSearchResult,
      textMatches: mockSearchResult.textMatches.filter(
        (match) => match.name !== "name",
      ),
    };

    renderComponent(modifiedSearchResult);

    expect(screen.getByRole("link", { name: "Test Norm" })).toBeInTheDocument();
  });

  it('displays "Titelzeile nicht vorhanden" when no title is available', () => {
    const modifiedSearchResult = {
      ...mockSearchResult,
      item: { ...mockSearchResult.item, name: "" },
      textMatches: mockSearchResult.textMatches.filter(
        (match) => match.name !== "name",
      ),
    };

    renderComponent(modifiedSearchResult);

    expect(
      screen.getByRole("link", { name: "Titelzeile nicht vorhanden" }),
    ).toBeInTheDocument();
  });

  it("correctly links to the norm page", () => {
    renderComponent();

    const link = screen.getByRole("link", { name: /Test Title/i });
    expect(link).toHaveAttribute(
      "href",
      `norms/${mockSearchResult.item.workExample.legislationIdentifier}`,
    );
  });

  it("displays highlighted text with correct class", () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "articles.text",
      text: "testing <mark>highlighted Text</mark> is here",
      location: null,
    };
    const modifiedSearchResult: SearchResult<LegislationWork> = {
      ...mockSearchResult,
      textMatches: [textMatch],
    };

    renderComponent(modifiedSearchResult);

    expect(screen.getByText("highlighted Text")).toBeInTheDocument();
  });

  it("filters HTML tags except mark, i, b", () => {
    const text =
      '<mark>mark</mark> <i>i</i> <b>b</b> <img src="" alt="do not show"> <div>div</div> plain_text.';
    const expectedSanitized =
      "<mark>mark</mark> <i>i</i> <b>b</b>  div plain_text.";
    const modifiedSearchResult: SearchResult<LegislationWork> = {
      ...mockSearchResult,
      textMatches: [
        {
          "@type": "SearchResultMatch",
          name: "name",
          text,
          location: null,
        },
        {
          "@type": "SearchResultMatch",
          name: text,
          text,
          location: "hauptteil_1",
        },
      ],
    };

    renderComponent(modifiedSearchResult);

    const titleHeading = screen.getAllByRole("heading")[0];
    expect(titleHeading?.innerHTML).toBe(expectedSanitized);

    const contentHeading = screen.getAllByRole("heading")[1];
    expect(contentHeading?.innerHTML).toBe(expectedSanitized);
  });
});
