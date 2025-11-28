import { render, screen } from "@testing-library/vue";
import { describe } from "vitest";
import AdministrativeDirectiveSearchResult from "~/components/Search/AdministrativeDirectiveSearchResult.vue";
import type { AdministrativeDirective, SearchResult } from "~/types";

const searchResult: SearchResult<AdministrativeDirective> = {
  item: {
    "@type": "AdministrativeDirective",
    "@id": "/v1/administrative-directive/KSNR000000001",
    documentNumber: "KSNR000000001",
    headline: "Verwaltungsvorschrift Überschrift",
    legislationAuthority: "Foo Authority",
    shortReport: "Inhalt eines Kurzreferats.",
    documentType: "VR",
    referenceNumbers: ["Foo - 123", "Bar - 123"],
    entryIntoForceDate: "2025-07-01",
  } as AdministrativeDirective,
  textMatches: [
    {
      "@type": "SearchResultMatch",
      name: "shortReport",
      text: "Inhalt eines Kurzreferats.",
      location: null,
    },
    {
      "@type": "SearchResultMatch",
      name: "headline",
      text: "Verwaltungsvorschrift Überschrift",
      location: null,
    },
  ],
};

function renderComponent({
  item = searchResult.item,
  textMatches = searchResult.textMatches,
}: Partial<SearchResult<AdministrativeDirective>> = {}) {
  const searchResult: SearchResult<AdministrativeDirective> = {
    item,
    textMatches,
  };

  return render(AdministrativeDirectiveSearchResult, {
    props: { searchResult, order: 0 },
    global: {
      stubs: {
        NuxtLink: {
          template: '<a :href="to"><slot /></a>',
          props: ["to"],
        },
      },
    },
  });
}

describe("AdministrativeDirectiveSearchResult", () => {
  describe("metadata", () => {
    it("renders documentType", () => {
      renderComponent();
      expect(screen.getByText("VR")).toBeVisible();
    });

    it("renders legislationAuthority", () => {
      renderComponent();
      expect(screen.getByText("Foo Authority")).toBeVisible();
    });

    it("renders first referenceNumber", () => {
      renderComponent();
      expect(screen.getByText("Foo - 123")).toBeVisible();

      // Don't render other referenceNumbers
      expect(screen.queryByText("Bar - 123")).not.toBeInTheDocument();
    });

    it("renders entryIntoForceDate", () => {
      renderComponent();
      expect(screen.getByText("01.07.2025")).toBeVisible();
    });
  });

  describe("headline", () => {
    it("renders plaintext headline with correct link", () => {
      renderComponent();
      const link = screen.getByRole("link", {
        name: "Verwaltungsvorschrift Überschrift",
      });
      expect(link).toHaveAttribute(
        "href",
        "/administrative-directive/KSNR000000001",
      );
    });

    it("renders markup headline with correct link", () => {
      renderComponent({
        textMatches: [
          {
            "@type": "SearchResultMatch",
            name: "headline",
            text: "Verwaltungsvorschrift <mark>Überschrift</mark>",
            location: null,
          },
        ],
      });
      const link = screen.getByRole("link", {
        name: "Verwaltungsvorschrift Überschrift",
      });
      expect(link).toHaveAttribute(
        "href",
        "/administrative-directive/KSNR000000001",
      );
      const mark = screen.getByText("Überschrift");
      expect(mark.tagName).toBe("MARK");
    });

    it("renders placeholder title with correct link", () => {
      renderComponent({
        textMatches: [],
      });

      const link = screen.getByRole("link", {
        name: "Titelzeile nicht vorhanden",
      });
      expect(link).toHaveAttribute(
        "href",
        "/administrative-directive/KSNR000000001",
      );
    });
  });

  describe("shortReport", () => {
    it("renders shortReport match without ellipses", () => {
      renderComponent();
      expect(screen.getByText("Inhalt eines Kurzreferats.")).toBeVisible();
    });

    it("renders shortReport match with ellipses at start", () => {
      renderComponent({
        item: {
          ...searchResult.item,
          shortReport: "Dies ist der lange Inhalt eines Kurzreferats.",
        },
      });
      expect(screen.getByText("… Inhalt eines Kurzreferats.")).toBeVisible();
    });

    it("renders shortReport match with ellipses at end", () => {
      renderComponent({
        item: {
          ...searchResult.item,
          shortReport: "Inhalt eines Kurzreferats. Weitere Inhalt.",
        },
      });
      expect(screen.getByText("Inhalt eines Kurzreferats. …")).toBeVisible();
    });

    it("renders shortReport match with ellipses at start and end", () => {
      renderComponent({
        item: {
          ...searchResult.item,
          shortReport:
            "Dies ist der lange Inhalt eines Kurzreferats. Weitere Inhalt.",
        },
      });
      expect(screen.getByText("… Inhalt eines Kurzreferats. …")).toBeVisible();
    });

    it("renders shortReport match with markup", () => {
      renderComponent({
        item: {
          ...searchResult.item,
          shortReport: "Inhalt eines Kurzreferats. Weiterer Inhalt.",
        },
        textMatches: [
          {
            "@type": "SearchResultMatch",
            name: "shortReport",
            text: "<b>Inhalt</b> <i>eines</i> <mark>Kurzreferats.</mark> <a>Weiterer Inhalt.</a>",
            location: null,
          },
        ],
      });
      expect(screen.getByText("Inhalt").tagName).toBe("B");
      expect(screen.getByText("eines").tagName).toBe("I");
      expect(screen.getByText("Kurzreferats.").tagName).toBe("MARK");
      // Does not render other tags
      expect(screen.getByText("Weiterer Inhalt.").tagName).not.toBe("A");
    });

    it("renders shortReport match with markup and ellipses", () => {
      renderComponent({
        item: {
          ...searchResult.item,
          shortReport: "Inhalt eines Kurzreferats. Weiterer Inhalt.",
        },
        textMatches: [
          {
            "@type": "SearchResultMatch",
            name: "shortReport",
            text: "<mark>Inhalt</mark> eines Kurzreferats.",
            location: null,
          },
        ],
      });
      expect(screen.getByText("Inhalt").tagName).toBe("MARK");
      expect(screen.getByText("eines Kurzreferats. …")).toBeVisible();
    });
  });
});
