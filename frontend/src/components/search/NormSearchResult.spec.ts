import { render, screen } from "@testing-library/vue";
import { describe, expect, it, vi, beforeEach, afterEach } from "vitest";
import NormSearchResult from "./NormSearchResult.vue";
import type { LegislationExpression, SearchResult, TextMatch } from "~/types";

const mockSearchResult: SearchResult<LegislationExpression> = {
  item: {
    name: "Test Norm",
    abbreviation: "TN",
    "@type": "Legislation",
    "@id": "eli/bund/bgbl-0/1999/ab/regelungstext-1",
    alternateName: "NoRM",
    legislationDate: "1999-12-14",
    datePublished: "1999-12-21",
    isPartOf: {
      name: "The Official Gazette",
    },
    hasPart: [],
    legislationIdentifier:
      "eli/bund/bgbl-0/1999/abc/1999-12-31/1/deu/regelungstext-1",
    encoding: [],
    tableOfContents: [],
    legislationLegalForce: "InForce",
    temporalCoverage: "2000-01-01/..",
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
      location: "PräöüÄÖÜambel",
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
  searchResult: SearchResult<LegislationExpression> = mockSearchResult,
  order: number = 0,
) {
  return render(NormSearchResult, {
    props: { searchResult, order },
    global: {
      stubs: {
        NuxtLink: {
          template: '<a :href="to"><slot /></a>',
          props: ["to"],
        },
      },
    },
  });
}

function withTemporalCoverage(temporalCoverage: string | undefined) {
  return {
    ...mockSearchResult,
    item: {
      ...mockSearchResult.item,
      temporalCoverage,
    },
  } as SearchResult<LegislationExpression>;
}

const mocks = vi.hoisted(() => {
  return {
    usePrivateFeaturesFlag: vi.fn().mockReturnValue(false),
  };
});

vi.mock("~/composables/usePrivateFeaturesFlag", () => {
  return { usePrivateFeaturesFlag: mocks.usePrivateFeaturesFlag };
});

describe("NormSearchResult", () => {
  beforeEach(() => {
    vi.resetAllMocks();
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
      `/norms/${mockSearchResult.item.legislationIdentifier}`,
    );
  });

  it("has accessible description linking to result type", () => {
    renderComponent();

    const link = screen.getByRole("link", {
      name: "Highlighted Test Title",
      description: "Norm",
    });
    expect(link).toBeInTheDocument();
  });

  it("displays highlighted text with correct class", () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "articles.text",
      text: "testing <mark>highlighted Text</mark> is here",
      location: null,
    };
    const modifiedSearchResult: SearchResult<LegislationExpression> = {
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
    const modifiedSearchResult: SearchResult<LegislationExpression> = {
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

  it("includes the location in the generated highlight URL for Article 1 properly encoded", () => {
    renderComponent();
    const articleHeading = screen.getByText("Article 1");
    expect(articleHeading).toBeInTheDocument();
    const link = articleHeading.closest("a");
    expect(link?.getAttribute("href")).contains("/PraeoeueAeOeUeambel");
  });

  describe("validity status badge", () => {
    beforeEach(() => {
      vi.useFakeTimers();
      vi.setSystemTime("2025-01-15");
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    it("displays green badge for currently valid norm", () => {
      const { container } = renderComponent(
        withTemporalCoverage("2024-01-01/2025-12-31"),
      );

      expect(screen.getByText("Aktuell gültig")).toBeInTheDocument();
      const badge = container.querySelector(".border-green-400");
      expect(badge).toBeInTheDocument();
    });

    it("displays yellow badge for future norm", () => {
      const { container } = renderComponent(
        withTemporalCoverage("2025-02-01/2025-12-31"),
      );

      expect(screen.getByText("Zukünftig in Kraft")).toBeInTheDocument();
      const badge = container.querySelector(".border-yellow-600");
      expect(badge).toBeInTheDocument();
    });

    it("displays red badge for expired norm", () => {
      const { container } = renderComponent(
        withTemporalCoverage("2020-01-01/2024-12-31"),
      );

      expect(screen.getByText("Außer Kraft")).toBeInTheDocument();
      const badge = container.querySelector(".border-red-400");
      expect(badge).toBeInTheDocument();
    });

    it("does not display badge when temporal coverage is undefined", () => {
      renderComponent(withTemporalCoverage(undefined));

      expect(screen.queryByText("Aktuell gültig")).not.toBeInTheDocument();
      expect(screen.queryByText("Zukünftig in Kraft")).not.toBeInTheDocument();
      expect(screen.queryByText("Außer Kraft")).not.toBeInTheDocument();
    });

    it("does not display badge when temporal coverage is completely open", () => {
      renderComponent(withTemporalCoverage("../.."));

      expect(screen.queryByText("Aktuell gültig")).not.toBeInTheDocument();
      expect(screen.queryByText("Zukünftig in Kraft")).not.toBeInTheDocument();
      expect(screen.queryByText("Außer Kraft")).not.toBeInTheDocument();
    });
  });
});
