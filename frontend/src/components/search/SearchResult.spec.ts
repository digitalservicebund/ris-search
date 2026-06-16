import { render } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLaw,
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
        } as SearchResult<CaseLaw>,
        order: 0,
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
        order: 0,
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
        order: 0,
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
        order: 0,
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
        order: 0,
      },
    });

    expect(container).toBeEmptyDOMElement();
  });
});
