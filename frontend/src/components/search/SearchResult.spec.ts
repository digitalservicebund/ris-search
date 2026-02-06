import { render } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SearchResultComponent from "./SearchResult.vue";
import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLaw,
  LegislationExpression,
  Literature,
  SearchResult,
} from "~/types";

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
          CaselawSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("caselaw-search-result-stub"),
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
          NormSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("norm-search-result-stub"),
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
          LiteratureSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("literature-search-result-stub"),
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
          AdministrativeDirectiveSearchResult: true,
        },
      },
    });

    expect(
      container.querySelector("administrative-directive-search-result-stub"),
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
