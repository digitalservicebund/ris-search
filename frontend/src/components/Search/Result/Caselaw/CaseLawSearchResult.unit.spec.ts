import { RouterLinkStub } from "@vue/test-utils";
import CaselawRecord from "@/components/Search/Result/Caselaw/CaselawSearchResult.vue";
import { describe, it, expect } from "vitest";
import type { CaseLaw, SearchResult, TextMatch } from "@/types";
import { mountSuspended } from "@nuxt/test-utils/runtime";

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

describe("CaselawSearchResult.vue", () => {
  it("renders the expected title", async () => {
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: searchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    expect(wrapper.get("a").text()).toBe("Decision Name — Test Headline");
  });

  it(`displays highlighted headline`, async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "headline",
      text: `testing <mark>highlighted headline</mark> is here`,
      location: null,
    };
    const caseLawSearchResult: SearchResult<CaseLaw> = {
      item: searchResult.item,
      textMatches: [textMatch],
    };
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: caseLawSearchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const highlightedElements = wrapper.findAll("mark");
    expect(highlightedElements).length(1);
    expect(highlightedElements[0].text()).toBe(`highlighted headline`);
  });

  it("displays highlighted file numbers", async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "fileNumbers",
      text: `testing <mark>highlighted file number</mark> is here`,
      location: null,
    };
    const caseLawSearchResult: SearchResult<CaseLaw> = {
      item: searchResult.item,
      textMatches: [textMatch],
    };
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: caseLawSearchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const highlightedElements = wrapper.findAll("mark");
    expect(highlightedElements).length(1);
    expect(highlightedElements[0].text()).toBe(`highlighted file number`);

    expect(wrapper.get("[aria-label='Aktenzeichen']").html()).toBe(
      `<span aria-label="Aktenzeichen">123, testing <mark>highlighted file number</mark> is here</span>`,
    );
  });

  it("displays highlighted text with correct class", async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "guidingPrinciple",
      text: "testing <mark>highlighted Text</mark> is here",
      location: null,
    };
    const caseLawSearchResult: SearchResult<CaseLaw> = {
      item: searchResult.item,
      textMatches: [textMatch],
    };
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: caseLawSearchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
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
    const caseLawSearchResult: SearchResult<CaseLaw> = {
      item: searchResult.item,
      textMatches: [
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
      ],
    };
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: caseLawSearchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const headlineContent = wrapper.find("h2 > span:has(mark)");
    expect(headlineContent.element.innerHTML).toBe(expectedSanitized);
    const contentItems = wrapper.findAll('[data-testid="highlighted-field"]');
    expect(contentItems.length).toBe(1);
    expect(contentItems[0].element.innerHTML).toBe(expectedSanitized);
  });

  it("displays static text when title is not present", async () => {
    const searchResultWithoutTitle = {
      item: { ...searchResult.item, headline: "" },
      textMatches: [],
    };
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: searchResultWithoutTitle, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    expect(wrapper.html()).toContain("Titelzeile nicht vorhanden");
  });
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
    it(`displays '${value}' when it is the only textMatch returned`, async () => {
      const searchResultWithoutTitle = {
        item: searchResult.item,
        textMatches: [
          {
            name: field,
            text: value,
            "@type": "SearchResultMatch",
            location: null,
          },
        ] as TextMatch[],
      };
      const wrapper = await mountSuspended(CaselawRecord, {
        props: { searchResult: searchResultWithoutTitle, order: 0 },
        stubs: {
          RouterLink: RouterLinkStub,
        },
      });
      const contentItems = wrapper.findAll('[data-testid="highlighted-field"]');
      expect(contentItems.length).toBe(1);
      expect(contentItems[0].text()).toBe(value);
    });
  });

  it("displays the first field in order only when there are no highlights if there are multiple fields", async () => {
    const wrapper = await mountSuspended(CaselawRecord, {
      props: {
        searchResult: {
          item: searchResult.item,
          textMatches: [
            {
              name: "guidingPrinciple",
              text: "Leitsatz.",
            },
            { name: "caseFacts", text: "Tatbestand." },
          ] as TextMatch[],
        },
        order: 0,
      },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const contentItems = wrapper.findAll('[data-testid="highlighted-field"]');
    expect(contentItems).toHaveLength(1);
    expect(contentItems[0].text()).toBe("Leitsatz.");
  });

  it("displays guiding principle first, if available, then up to 3 other matching items", async () => {
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
    const caseLawSearchResult: SearchResult<CaseLaw> = {
      item: searchResult.item,
      textMatches: textMatches,
    };
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: caseLawSearchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const contentItems = wrapper.findAll('[data-testid="highlighted-field"]');
    expect(contentItems.length).toBe(4);
    expect(contentItems[0].element.innerHTML).toBe(guidingPrinciple.text);
    expect(contentItems[1].element.innerHTML).toBe(textMatches[0].text);
    expect(contentItems[2].element.innerHTML).toBe(textMatches[2].text);
    expect(contentItems[3].element.innerHTML).toBe(textMatches[3].text);
  });

  it("returns an empty array when there are no caselaw fields", async () => {
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
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: searchResultWithoutFields, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const contentItems = wrapper.findAll('[data-testid="highlighted-field"]');
    expect(contentItems.length).toBe(0);
  });

  it("shows only the first textMatch if none contain marks", async () => {
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
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: searchResultWithoutHighlights, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const contentItems = wrapper.findAll('[data-testid="highlighted-field"]');
    expect(contentItems.length).toBe(1);
    expect(contentItems[0].text()).toBe("Guiding Principle.");
  });

  it("returns the first field from caselaw fields and up to three highlighted fields if highlights are present", async () => {
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
    const caseLawSearchResult: SearchResult<CaseLaw> = {
      item: searchResult.item,
      textMatches: textMatches,
    };
    const wrapper = await mountSuspended(CaselawRecord, {
      props: { searchResult: caseLawSearchResult, order: 0 },
      stubs: {
        RouterLink: RouterLinkStub,
      },
    });
    const contentItems = wrapper.findAll('[data-testid="highlighted-field"]');
    expect(contentItems).toHaveLength(4);
    expect(contentItems[0].element.innerHTML).toBe("Guiding Principle.");
    expect(contentItems[1].element.innerHTML).toBe(textMatches[1].text);
    expect(contentItems[2].element.innerHTML).toBe(textMatches[2].text);
    expect(contentItems[3].element.innerHTML).toBe(textMatches[3].text);
  });
});
