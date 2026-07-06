import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe } from "vitest";
import AdministrativeDirectiveSearchResult from "~/components/search/AdministrativeDirectiveSearchResult.vue";
import type { AdministrativeDirective, SearchResult } from "~/types/api";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({
    fullPath: "/search?query=test&documentKind=VS",
  })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

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
  textMatches: [],
};

async function renderComponent({
  item = searchResult.item,
  textMatches = searchResult.textMatches,
}: Partial<SearchResult<AdministrativeDirective>> = {}) {
  const result: SearchResult<AdministrativeDirective> = {
    item,
    textMatches,
  };

  return await renderSuspended(AdministrativeDirectiveSearchResult, {
    props: { searchResult: result, order: 0 },
    global: {
      stubs: {
        NuxtLink: {
          template:
            '<a :href="to.path ?? to" :data-from="to.query?.from"><slot /></a>',
          props: ["to"],
        },
      },
    },
  });
}

describe("AdministrativeDirectiveSearchResult", () => {
  describe("metadata", () => {
    it("renders documentType", async () => {
      await renderComponent();
      expect(screen.getByText("VR")).toBeVisible();
    });

    it("renders legislationAuthority", async () => {
      await renderComponent();
      expect(screen.getByText("Foo Authority")).toBeVisible();
    });

    it("renders first referenceNumber", async () => {
      await renderComponent();
      expect(screen.getByText("Foo - 123")).toBeVisible();

      // Don't render other referenceNumbers
      expect(screen.queryByText("Bar - 123")).not.toBeInTheDocument();
    });

    it("renders entryIntoForceDate", async () => {
      await renderComponent();
      expect(screen.getByText("01.07.2025")).toBeVisible();
    });
  });

  describe("headline", () => {
    it("renders plaintext headline as link", async () => {
      await renderComponent();
      expect(
        screen.getByRole("link", {
          name: "Verwaltungsvorschrift Überschrift",
        }),
      ).toBeVisible();
    });

    it("has accessible description linking to result type", async () => {
      await renderComponent();
      expect(
        screen.getByRole("link", {
          name: "Verwaltungsvorschrift Überschrift",
          description: "VR",
        }),
      ).toBeInTheDocument();
    });

    it("renders markup headline with correct link", async () => {
      await renderComponent({
        textMatches: [
          {
            "@type": "SearchResultMatch",
            name: "headline",
            text: "Verwaltungsvorschrift <mark>Überschrift</mark>",
            location: undefined,
          },
        ],
      });
      expect(
        screen.getByRole("link", {
          name: "Verwaltungsvorschrift Überschrift",
        }),
      ).toBeVisible();
      const mark = screen.getByText("Überschrift");
      expect(mark.tagName).toBe("MARK");
    });

    it("uses item headline as fallback when no text match is present", async () => {
      await renderComponent({
        textMatches: [],
      });

      expect(
        screen.getByRole("link", {
          name: "Verwaltungsvorschrift Überschrift",
        }),
      ).toBeVisible();
    });

    it("renders placeholder title when neither text match nor item headline is present", async () => {
      await renderComponent({
        item: { ...searchResult.item, headline: undefined },
        textMatches: [],
      });

      expect(
        screen.getByRole("link", {
          name: "Titelzeile nicht vorhanden",
        }),
      ).toBeVisible();
    });
  });

  describe("preview sections", () => {
    it("renders matches", async () => {
      await renderComponent({
        textMatches: [
          {
            "@type": "SearchResultMatch",
            name: "shortReport",
            text: "testing <mark>highlighted Text</mark> is here",
            location: undefined,
          },
          {
            "@type": "SearchResultMatch",
            name: "tableOfContentsEntries",
            text: "<mark>I. Einführung</mark>",
            location: undefined,
          },
        ],
      });

      expect(
        screen.getByRole("link", { name: "Kurzreferat:" }),
      ).toBeInTheDocument();
      const linkMark = screen.getByText("highlighted Text");
      expect(linkMark.tagName).toBe("MARK");

      expect(screen.getByRole("link", { name: "Inhalt:" })).toBeInTheDocument();
      const shortReportMark = screen.getByText("I. Einführung");
      expect(shortReportMark.tagName).toBe("MARK");
    });

    it("filters HTML tags except mark, i, b", async () => {
      const text =
        '<mark>mark</mark> <i>i</i> <b>b</b> <img src="" alt="do not show"> <div>div</div> plain_text.';
      const expectedSanitized =
        "<mark>mark</mark> <i>i</i> <b>b</b>  div plain_text.";

      await renderComponent({
        textMatches: [
          {
            "@type": "SearchResultMatch",
            name: "shortReport",
            text,
            location: undefined,
          },
        ],
      });

      const items = screen.getAllByTestId("highlighted-field");
      expect(items).toHaveLength(1);
      expect(items[0]?.innerHTML).toBe(expectedSanitized);
    });

    it("does not render a section when the text match has no highlight", async () => {
      await renderComponent({
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

  it("includes the current search URL as query param in the detail page link", async () => {
    useRouteMock.mockReturnValue({
      fullPath: "/search?query=Vorschrift&documentKind=VS&pageIndex=0",
    });

    await renderComponent();

    const link = screen.getByRole("link", {
      name: "Verwaltungsvorschrift Überschrift",
    });
    expect(link).toHaveAttribute(
      "data-from",
      "/search?query=Vorschrift&documentKind=VS&pageIndex=0",
    );
  });

  it("includes the current search URL as query param in preview section links", async () => {
    useRouteMock.mockReturnValue({
      fullPath: "/search?query=Vorschrift&documentKind=VS&pageIndex=0",
    });

    await renderComponent({
      textMatches: [
        {
          "@type": "SearchResultMatch",
          name: "shortReport",
          text: "testing <mark>highlighted</mark> text",
          location: undefined,
        },
      ],
    });

    const sectionLink = screen.getByRole("link", { name: "Kurzreferat:" });
    expect(sectionLink).toHaveAttribute(
      "data-from",
      "/search?query=Vorschrift&documentKind=VS&pageIndex=0",
    );
  });
});
