import { mount } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import SearchResultComponent from "./SearchResult.vue";
import type {
  AnyDocument,
  CaseLaw,
  LegislationWork,
  SearchResult,
} from "~/types";

describe("SearchResult.vue", () => {
  it("shows the correct result for Decisions", () => {
    const wrapper = mount(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Decision" },
        } as SearchResult<CaseLaw>,
        order: 0,
      },
      shallow: true,
    });

    expect(wrapper.find("caselaw-search-result-stub").isVisible()).toBe(true);
  });
  it("shows the correct result for Legislation", () => {
    const wrapper = mount(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "Legislation" },
        } as SearchResult<LegislationWork>,
        order: 0,
      },
      shallow: true,
    });

    expect(wrapper.find("norm-search-result-stub").isVisible()).toBe(true);
  });
  it("shows nothing for another type", () => {
    const wrapper = mount(SearchResultComponent, {
      props: {
        searchResult: {
          item: { "@type": "OtherType" },
        } as unknown as SearchResult<AnyDocument>,
        order: 0,
      },
      shallow: true,
    });

    expect(wrapper.html()).toBe("<!--v-if-->");
  });
});
