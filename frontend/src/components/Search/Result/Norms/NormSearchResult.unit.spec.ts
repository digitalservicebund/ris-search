import { mountSuspended } from "@nuxt/test-utils/runtime";
import { mount, RouterLinkStub } from "@vue/test-utils";
import { describe, it, expect, vi, afterEach } from "vitest";
import NormSearchResult from "./NormSearchResult.vue";
import type { LegislationWork, SearchResult, TextMatch } from "~/types";

const NuxtLinkStub = {
  name: "NuxtLink",
  props: ["to"],
  template: '<a :href="to"><slot></slot></a>',
};

const mocks = vi.hoisted(() => {
  return {
    isPrototypeProfile: vi.fn().mockReturnValue(false),
  };
});

vi.mock("~/composables/useProfile", () => {
  return {
    useProfile: () => {
      return { isPrototypeProfile: mocks.isPrototypeProfile };
    },
  };
});

describe("NormSearchResult.vue", () => {
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

  const customMount = (props: {
    searchResult: SearchResult<LegislationWork>;
    order: number;
  }) => {
    return mount(NormSearchResult, {
      props,
      global: {
        stubs: {
          NuxtLink: NuxtLinkStub,
        },
      },
    });
  };

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders correctly with all props", () => {
    const wrapper = customMount({
      searchResult: mockSearchResult,
      order: 0,
    });

    expect(wrapper.text()).toContain("TN");
    expect(wrapper.text()).toContain("Norm");
    expect(wrapper.text()).toContain("01.01.2000");
    expect(wrapper.text()).not.toContain("14.12.1999");
    expect(wrapper.get("a").element.innerHTML).toBe(
      "<div>Highlighted <mark>Test Title</mark></div>",
    );
    expect(wrapper.find("[data-testid='highlights'] div").text()).contains(
      "Article 1",
    );
    expect(wrapper.find("[data-testid='highlights'] div").text()).contains(
      "Example Text 1",
    );
    expect(wrapper.findAll('[data-testid="highlights"] > div')).toHaveLength(4); // 4 relevant highlights
  });

  it("renders ausfertigungs datum when in prototype environment", () => {
    mocks.isPrototypeProfile.mockReturnValue(true);

    const wrapper = customMount({
      searchResult: mockSearchResult,
      order: 0,
    });

    expect(wrapper.text()).toContain("14.12.1999");
    expect(wrapper.text()).not.toContain("01.01.2000");
  });

  it("uses item name when official title is not available", () => {
    const modifiedSearchResult = {
      ...mockSearchResult,
      textMatches: mockSearchResult.textMatches.filter(
        (match) => match.name !== "name",
      ),
    };

    const wrapper = customMount({
      searchResult: modifiedSearchResult,
      order: 0,
    });

    expect(wrapper.get("a").text()).toBe("Test Norm");
  });

  it('displays "Titelzeile nicht vorhanden" when no title is available', () => {
    const modifiedSearchResult = {
      ...mockSearchResult,
      item: { ...mockSearchResult.item, name: "" },
      textMatches: mockSearchResult.textMatches.filter(
        (match) => match.name !== "name",
      ),
    };

    const wrapper = customMount({
      searchResult: modifiedSearchResult,
      order: 0,
    });

    expect(wrapper.get("a").text()).toBe("Titelzeile nicht vorhanden");
  });

  it("correctly links to the norm page", () => {
    const wrapper = customMount({
      searchResult: mockSearchResult,
      order: 0,
    });

    const link = wrapper.find("a");
    expect(link.attributes("href")).toBe(
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
    const wrapper = customMount({
      searchResult: modifiedSearchResult,
      order: 0,
    });
    const highlightedElements = wrapper.findAll("mark");
    expect(highlightedElements).length(1);
    expect(highlightedElements[0].text()).toBe("highlighted Text");
  });

  it("filters HTML tags except mark, i, b", async () => {
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
    const wrapper = await mountSuspended(NormSearchResult, {
      props: { searchResult: modifiedSearchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const titleContent = wrapper.find("a > div");
    expect(titleContent.element.innerHTML).toBe(expectedSanitized);

    const contentItems = wrapper.findAll('[data-testid="highlights"] a div');
    expect(contentItems.length).toBe(1);
    expect(contentItems[0].element.innerHTML).toBe(expectedSanitized);
  });
});
