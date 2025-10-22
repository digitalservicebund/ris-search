import { render, screen } from "@testing-library/vue";
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
    normReferences: ["GG, Art 6 Abs 2 S 1, 1949-05-23"],
    headline: "Eine Untersuchung der juristischen Methoden im 21. Jahrhundert",
    alternativeHeadline: "Study of Legal Methodologies in the 21st Century",
    headlineAdditions: "Zusatz zur Hauptüberschrift",
    authors: ["Mustermann, Max", "Musterfrau, Erika"],
    collaborators: ["Doe, John", "Doe, Jane"],
    originators: ["FOO"],
    conferenceNotes: ["Internationaler Kongress 2025, Berlin, GER"],
    languages: ["deu", "eng"],
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

function renderComponent({
  item = searchResult.item,
  textMatches = [],
}: Partial<SearchResult<Literature>> = {}) {
  const searchResult: SearchResult<Literature> = {
    item,
    textMatches,
  };

  return render(LiteratureSearchResult, {
    props: { searchResult, order: 0 },
    global: { stubs: { RouterLink: true }, renderStubDefaultSlot: true },
  });
}

describe("LiteratureSearchResult.vue", () => {
  it("renders the expected title", async () => {
    renderComponent({});
    expect(
      screen.getByText(
        "Eine Untersuchung der juristischen Methoden im 21. Jahrhundert",
      ),
    ).toBeInTheDocument();
  });

  it("displays highlighted headline when mainTitle", async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "mainTitle",
      text: `testing <mark>highlighted main title</mark> is here`,
      location: null,
    };

    renderComponent({ textMatches: [textMatch] });

    const mark = screen.getByText("highlighted main title");
    expect(mark.tagName).toBe("MARK");
  });

  it("displays highlighted headline when documentary title", async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "documentaryTitle",
      text: `testing <mark>highlighted documentary title</mark> is here`,
      location: null,
    };

    // Clone the item but set headline to null
    const itemWithoutHeadline = { ...searchResult.item, headline: null };

    renderComponent({ item: itemWithoutHeadline, textMatches: [textMatch] });

    const mark = screen.getByText("highlighted documentary title");
    expect(mark.tagName).toBe("MARK");
  });

  it("displays alternative title when headline is not present", async () => {
    const itemWithoutHeadline = { ...searchResult.item, headline: "" };
    renderComponent({ item: itemWithoutHeadline });
    expect(
      screen.getByText("Study of Legal Methodologies in the 21st Century"),
    ).toBeInTheDocument();
  });

  it("displays highlighted text with correct class", async () => {
    const textMatch: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: "testing <mark>highlighted Text</mark> is here",
      location: null,
    };

    renderComponent({ textMatches: [textMatch] });

    const mark = screen.getByText("highlighted Text");
    expect(mark.tagName).toBe("MARK");
  });

  it("renders full shortReport when no match is present", async () => {
    renderComponent({});
    expect(
      screen.getByText(/Dieses Werk analysiert die Entwicklung/),
    ).toBeInTheDocument();
  });

  it("applies line-clamp-3 class only when no highlight exists", async () => {
    // No highlight
    const { rerender } = renderComponent({});
    const span = screen.getByTestId("highlighted-field");
    expect(span).toHaveClass("line-clamp-3");

    // With highlight
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `<mark>Dieses</mark> Werk analysiert`,
      location: null,
    };
    await rerender({
      searchResult: { item: searchResult.item, textMatches: [match] },
    });
    expect(screen.getByTestId("highlighted-field")).not.toHaveClass(
      "line-clamp-3",
    );
  });

  it("highlights a word in the middle of shortReport", async () => {
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `… Rolle von <mark>Präzedenzfällen</mark> in modernen Gerichtsbarkeiten …`,
      location: null,
    };

    renderComponent({ textMatches: [match] });

    const mark = screen.getByText("Präzedenzfällen");
    expect(mark).toBeInTheDocument();
    expect(mark.tagName).toBe("MARK");
  });

  it("highlights a word at the end of shortReport", async () => {
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `… automatisierter <mark>Entscheidungsfindung</mark>`,
      location: null,
    };

    renderComponent({ textMatches: [match] });

    const mark = screen.getByText("Entscheidungsfindung");
    expect(mark).toBeInTheDocument();
    expect(mark.tagName).toBe("MARK");
  });

  it("limits shortReport snippet length and preserves highlight", async () => {
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `… Rolle von <mark>Präzedenzfällen</mark> in modernen Gerichtsbarkeiten …`,
      location: null,
    };

    renderComponent({ textMatches: [match] });

    const snippet = screen.getByTestId("highlighted-field");
    expect(snippet).toHaveTextContent("Präzedenzfällen");
    expect(snippet.textContent?.length ?? 0).toBeLessThanOrEqual(280);
  });

  it("applies line-clamp-3 class only when no highlight exists", async () => {
    // No match
    const { rerender } = renderComponent({});
    let span = screen.getByTestId("highlighted-field");
    expect(span).toHaveClass("line-clamp-3");

    // With match
    const match: TextMatch = {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: `<mark>Dieses</mark> Werk analysiert`,
      location: null,
    };
    await rerender({
      searchResult: { item: searchResult.item, textMatches: [match] },
    });
    span = screen.getByTestId("highlighted-field");
    expect(span).not.toHaveClass("line-clamp-3");
  });
});
