import { mountSuspended } from "@nuxt/test-utils/runtime";
import { RouterLinkStub } from "@vue/test-utils";
import { describe, expect, it } from "vitest";
import LiteratureSearchResult from "~/components/Search/Result/Literature/LiteratureSearchResult.vue";
import type { Literature, SearchResult, TextMatch } from "~/types";

const searchResult: SearchResult<Literature> = {
  item: {
    "@id": "",
    "@type": "Literature",
    inLanguage: "de",
    documentNumber: "LIT-123",
    recordingDate: "2023-02-15",
    yearsOfPublication: ["2021", "2022", "2023"],
    documentTypes: ["Book", "Article"],
    dependentReferences: ["LIT-122", "LIT-121"],
    independentReferences: ["LIT-124", "LIT-125"],
    headline: "Eine Untersuchung der juristischen Methoden im 21. Jahrhundert",
    documentaryTitle: "Study of Legal Methodologies in the 21st Century",
    authors: ["Dr. Max Mustermann", "Prof. Erika Musterfrau"],
    collaborators: ["John Doe (Translator)", "Jane Doe (Editor)"],
    shortReport: `Dieses Werk analysiert die Entwicklung der juristischen Methoden seit Beginn des 21. Jahrhunderts mit besonderem Fokus auf europäische Rechtssysteme.
Es werden die Unterschiede zwischen nationalen Rechtstraditionen dargestellt und ihre Auswirkungen auf internationale Verträge erläutert.
Darüber hinaus untersucht die Studie die Rolle von Präzedenzfällen in modernen Gerichtsbarkeiten und diskutiert aktuelle Trends in der Gesetzesauslegung.
Abschließend gibt das Werk Empfehlungen für die praktische Anwendung juristischer Methoden in Forschung und Lehre, insbesondere im Kontext digitaler Rechtssysteme und automatisierter Entscheidungsfindung.`,
    outline:
      "1. Einführung\n2. Historischer Überblick\n3. Aktuelle Entwicklungen\n4. Schlussfolgerungen",
    encoding: [],
  },
  textMatches: [],
};

async function mountComponent({
  item = searchResult.item,
  textMatches = [],
}: Partial<SearchResult<Literature>>) {
  const searchResult: SearchResult<Literature> = {
    item,
    textMatches,
  };
  return await mountSuspended(LiteratureSearchResult, {
    props: { searchResult, order: 0 },
    stubs: {
      RouterLink: RouterLinkStub,
    },
  });
}

describe("LiteratureSearchResult.vue", () => {
  it("renders the expected title", async () => {
    const wrapper = await mountComponent({});
    expect(wrapper.get("a").text()).toBe(
      "Eine Untersuchung der juristischen Methoden im 21. Jahrhundert",
    );
  });

  it("displays highlighted headline", async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "headline",
      text: `testing <mark>highlighted headline</mark> is here`,
      location: null,
    };
    const wrapper = await mountComponent({ textMatches: [textMatch] });
    const highlightedElements = wrapper.findAll("mark");
    expect(highlightedElements).length(1);
    expect(highlightedElements[0].text()).toBe(`highlighted headline`);
  });

  it("displays alternative title when headline is not present", async () => {
    const searchResultWithoutHeadline = {
      item: { ...searchResult.item, headline: "" },
      textMatches: [],
    };
    const wrapper = await mountComponent(searchResultWithoutHeadline);
    expect(wrapper.html()).toContain(
      "Study of Legal Methodologies in the 21st Century",
    );
  });

  it("displays highlighted text with correct class", async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: "testing <mark>highlighted Text</mark> is here",
      location: null,
    };
    const wrapper = await mountComponent({ textMatches: [textMatch] });

    const highlightedElements = wrapper.findAll("mark");
    expect(highlightedElements).length(1);
    expect(highlightedElements[0].text()).toBe("highlighted Text");
  });

  it("renders full shortReport when no match is present", async () => {
    const wrapper = await mountComponent({});
    expect(wrapper.text()).toContain(
      "Dieses Werk analysiert die Entwicklung der juristischen Methoden",
    );
  });

  it("highlights a word at the beginning of shortReport", async () => {
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `<mark>Dieses</mark> Werk analysiert die Entwicklung der juristischen Methoden`,
      location: null,
    };
    const wrapper = await mountComponent({ textMatches: [match] });

    const mark = wrapper.find("mark");
    expect(mark.exists()).toBe(true);
    expect(mark.text()).toBe("Dieses");
  });

  it("highlights a word in the middle of shortReport", async () => {
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `… Rolle von <mark>Präzedenzfällen</mark> in modernen Gerichtsbarkeiten …`,
      location: null,
    };
    const wrapper = await mountComponent({ textMatches: [match] });

    const mark = wrapper.find("mark");
    expect(mark.exists()).toBe(true);
    expect(mark.text()).toBe("Präzedenzfällen");
  });

  it("highlights a word at the end of shortReport", async () => {
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `… automatisierter <mark>Entscheidungsfindung</mark>`,
      location: null,
    };
    const wrapper = await mountComponent({ textMatches: [match] });

    const mark = wrapper.find("mark");
    expect(mark.exists()).toBe(true);
    expect(mark.text()).toBe("Entscheidungsfindung");
  });

  it("limits shortReport snippet length and preserves highlight", async () => {
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `… Rolle von <mark>Präzedenzfällen</mark> in modernen Gerichtsbarkeiten …`,
      location: null,
    };
    const wrapper = await mountComponent({ textMatches: [match] });

    const snippet = wrapper.find("[data-testid='highlighted-field']");
    expect(snippet.text()).toContain("Präzedenzfällen");
    expect(snippet.text().length).toBeLessThanOrEqual(280); // small tolerance for ellipses
  });

  it("applies line-clamp-3 class only when no highlight exists", async () => {
    // No match
    let wrapper = await mountComponent({});
    let span = wrapper.find("[data-testid='highlighted-field']");
    expect(span.classes()).toContain("line-clamp-3");

    // With match
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `<mark>Dieses</mark> Werk analysiert`,
      location: null,
    };
    wrapper = await mountComponent({ textMatches: [match] });
    span = wrapper.find("[data-testid='highlighted-field']");
    expect(span.classes()).not.toContain("line-clamp-3");
  });
});
