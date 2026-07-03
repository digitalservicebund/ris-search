import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { render, screen } from "@testing-library/vue";
import { describe } from "vitest";
import LiteratureSearchResult from "~/components/search/LiteratureSearchResult.vue";
import type { Literature, SearchResult, TextMatch } from "~/types/api";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({
    fullPath: "/search?query=test&documentKind=L",
  })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

const searchResult: SearchResult<Literature> = {
  item: {
    "@id": "",
    "@type": "Literature",
    inLanguage: "de",
    documentNumber: "LIT-123",
    yearsOfPublication: ["2021", "2022", "2023"],
    documentTypes: ["Book", "Article"],
    dependentReferences: ["DEP-122", "DEP-121"],
    independentReferences: ["INDEP-124", "INDEP-125"],
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
    universityNotes: [],
    literatureType: "uli",
    editors: [],
    founder: [],
    publishers: [],
    publisherOrganizations: [],
    publishingHouses: [],
    edition: undefined,
    volumes: [],
    internationalIdentifiers: [],
  },
  textMatches: [],
};

function renderComponent({
  item = searchResult.item,
  textMatches = [],
}: Partial<SearchResult<Literature>> = {}) {
  const result: SearchResult<Literature> = {
    item,
    textMatches,
  };

  return render(LiteratureSearchResult, {
    props: { searchResult: result, order: 0 },
    global: {
      stubs: {
        NuxtLink: {
          template:
            '<a :href="JSON.stringify(to)" :aria-describedby="ariaDescribedby"><slot /></a>',
          props: ["to", "ariaDescribedby"],
        },
      },
    },
  });
}

describe("LiteratureSearchResult", () => {
  describe("metadata", () => {
    it("renders first documentType", async () => {
      renderComponent();
      expect(screen.getByText("Book")).toBeVisible();
    });

    it("renders first dependentReference", async () => {
      renderComponent();
      expect(screen.getByText("DEP-122")).toBeVisible();
    });

    it("renders first independentReference if no dependentReference exists", async () => {
      renderComponent({
        item: {
          ...searchResult.item,
          dependentReferences: [],
        },
      });
      expect(screen.getByText("INDEP-124")).toBeVisible();
    });

    it("renders first year of publication", async () => {
      renderComponent();
      expect(screen.getByText("2021")).toBeVisible();
    });
  });

  describe("headline", () => {
    it("renders the expected title", async () => {
      renderComponent({});
      expect(
        screen.getByText(
          "Eine Untersuchung der juristischen Methoden im 21. Jahrhundert",
        ),
      ).toBeInTheDocument();
    });

    it("has accessible description linking to result type", async () => {
      renderComponent({});
      expect(
        screen.getByRole("link", {
          name: "Eine Untersuchung der juristischen Methoden im 21. Jahrhundert",
          description: "Book",
        }),
      ).toBeInTheDocument();
    });

    it("renders placeholder title if title is missing", async () => {
      const searchResultWithoutTitle = {
        item: {
          ...searchResult.item,
          headline: undefined,
          alternativeHeadline: undefined,
        },
        textMatches: [],
      };
      renderComponent(searchResultWithoutTitle);
      expect(
        screen.getByText("Titelzeile nicht vorhanden"),
      ).toBeInTheDocument();
    });

    it("displays highlighted headline when main title has highlights", async () => {
      const textMatch: TextMatch = {
        "@type": "SearchResultMatch",
        name: "mainTitle",
        text: `testing <mark>highlighted main title</mark> is here`,
        location: undefined,
      };

      renderComponent({ textMatches: [textMatch] });

      const mark = screen.getByText("highlighted main title");
      expect(mark.tagName).toBe("MARK");
    });

    it("displays highlighted headline when documentary title has markup", async () => {
      const textMatch: TextMatch = {
        "@type": "SearchResultMatch",
        name: "documentaryTitle",
        text: `testing <mark>highlighted documentary title</mark> is here`,
        location: undefined,
      };

      const itemWithoutHeadline = { ...searchResult.item, headline: undefined };

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
  });

  describe("preview sections", () => {
    it("renders no highlights when text matches is empty", () => {
      renderComponent({});
      expect(screen.queryAllByTestId("highlighted-field")).toHaveLength(0);
    });

    it("renders matches", () => {
      const textMatches: TextMatch[] = [
        {
          "@type": "SearchResultMatch",
          name: "outline",
          text: "<mark>I. Einführung</mark>",
          location: undefined,
        },
        {
          "@type": "SearchResultMatch",
          name: "shortReport",
          text: "testing <mark>highlighted Text</mark> is here",
          location: undefined,
        },
      ];

      renderComponent({ textMatches });

      expect(
        screen.getByRole("link", { name: "Gliederung:" }),
      ).toBeInTheDocument();
      const outlineMark = screen.getByText("I. Einführung");
      expect(outlineMark.tagName).toBe("MARK");

      expect(
        screen.getByRole("link", { name: "Kurzreferat:" }),
      ).toBeInTheDocument();
      const shortReportMark = screen.getByText("highlighted Text");
      expect(shortReportMark.tagName).toBe("MARK");
    });

    it("filters HTML tags except mark, i, b", () => {
      const text =
        '<mark>mark</mark> <i>i</i> <b>b</b> <img src="" alt="do not show"> <div>div</div> plain_text.';
      const expectedSanitized =
        "<mark>mark</mark> <i>i</i> <b>b</b>  div plain_text.";

      const textMatches: TextMatch[] = [
        {
          "@type": "SearchResultMatch",
          name: "shortReport",
          text,
          location: undefined,
        },
      ];

      renderComponent({ textMatches });

      const contentItems = screen.getAllByTestId("highlighted-field");
      expect(contentItems).toHaveLength(1);
      expect(contentItems[0]?.innerHTML).toBe(expectedSanitized);
    });

    it("does not render a section when the text match has no highlight", () => {
      renderComponent({
        textMatches: [
          {
            "@type": "SearchResultMatch",
            name: "shortReport",
            text: "plain text without any highlight",
            location: undefined,
          },
        ],
      });

      expect(screen.queryAllByTestId("highlighted-field")).toHaveLength(0);
    });
  });

  it("includes the current search URL as query param in the detail page link", () => {
    useRouteMock.mockReturnValue({
      fullPath: "/search?query=Recht&documentKind=L&pageIndex=3",
    });

    renderComponent({});

    const link = screen.getByRole("link", {
      name: "Eine Untersuchung der juristischen Methoden im 21. Jahrhundert",
    });
    const href = JSON.parse(link.getAttribute("href") ?? "{}");
    expect(href.query?.from).toBe(
      "/search?query=Recht&documentKind=L&pageIndex=3",
    );
  });
});
