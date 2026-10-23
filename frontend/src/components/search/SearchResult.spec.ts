import { render } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLawSearchSchema,
  LegislationExpression,
  Literature,
  SearchResult,
} from "~/types/api";
import SearchResultComponent from "./SearchResult.vue";

describe("SearchResult", () => {
  it("shows the correct result for caselaw", () => {
    const { container } = render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Decision" },
        } as SearchResult<CaseLawSearchSchema>,
      },
      global: {
        stubs: {
          SearchCaselawSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("search-caselaw-search-result-stub"),
    ).toBeInTheDocument();
  });

  it("shows the correct result for norm", () => {
    const { container } = render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Legislation" },
        } as SearchResult<LegislationExpression>,
      },
      global: {
        stubs: {
          SearchNormSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("search-norm-search-result-stub"),
    ).toBeInTheDocument();
  });

  it("shows the correct result for literature", () => {
    const { container } = render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Literature" },
        } as SearchResult<Literature>,
      },
      global: {
        stubs: {
          SearchLiteratureSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("search-literature-search-result-stub"),
    ).toBeInTheDocument();
  });

  it("shows the correct result for administrative directive", () => {
    const { container } = render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "AdministrativeDirective" },
        } as SearchResult<AdministrativeDirective>,
      },
      global: {
        stubs: {
          SearchAdministrativeDirectiveSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector(
        "search-administrative-directive-search-result-stub",
      ),
    ).toBeInTheDocument();
  });

  it("shows nothing for another type", () => {
    const { container } = render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "OtherType" },
        } as unknown as SearchResult<AnyDocument>,
      },
    });

    expect(container).toBeEmptyDOMElement();
  });

  it("forwards the heading level to the result component", () => {
    const { container } = render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Decision" },
        } as SearchResult<CaseLawSearchSchema>,
        headingLevel: "3" as const,
      },
      global: {
        stubs: {
          SearchCaselawSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("search-caselaw-search-result-stub"),
    ).toHaveAttribute("headinglevel", "3");
  });

  it("defaults the heading level to 2", () => {
    const { container } = render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Decision" },
        } as SearchResult<CaseLawSearchSchema>,
      },
      global: {
        stubs: {
          SearchCaselawSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("search-caselaw-search-result-stub"),
    ).toHaveAttribute("headinglevel", "2");
  });
});
