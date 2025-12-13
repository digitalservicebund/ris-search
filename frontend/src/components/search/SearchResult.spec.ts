import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SearchResultComponent from "./SearchResult.vue";
import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLaw,
  LegislationWork,
  Literature,
  SearchResult,
} from "~/types";

describe("SearchResult", () => {
  it("shows the correct result for caselaw", () => {
    render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Decision" },
        } as SearchResult<CaseLaw>,
        order: 0,
      },
      global: {
        stubs: {
          CaselawSearchResult: {
            template: "caselaw-search-result-stub",
          },
        },
      },
    });

    expect(screen.getByText("caselaw-search-result-stub")).toBeInTheDocument();
  });

  it("shows the correct result for norm", () => {
    render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Legislation" },
        } as SearchResult<LegislationWork>,
        order: 0,
      },
      global: {
        stubs: {
          NormSearchResult: {
            template: "norm-search-result-stub",
          },
        },
      },
    });

    expect(screen.getByText("norm-search-result-stub")).toBeInTheDocument();
  });

  it("shows the correct result for literature", () => {
    render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Literature" },
        } as SearchResult<Literature>,
        order: 0,
      },
      global: {
        stubs: {
          LiteratureSearchResult: {
            template: "literature-search-result-stub",
          },
        },
      },
    });

    expect(
      screen.getByText("literature-search-result-stub"),
    ).toBeInTheDocument();
  });

  it("shows the correct result for administrative directive", () => {
    render(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "AdministrativeDirective" },
        } as SearchResult<AdministrativeDirective>,
        order: 0,
      },
      global: {
        stubs: {
          AdministrativeDirectiveSearchResult: {
            template: "administrative-directive-search-result-stub",
          },
        },
      },
    });

    expect(
      screen.getByText("administrative-directive-search-result-stub"),
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
