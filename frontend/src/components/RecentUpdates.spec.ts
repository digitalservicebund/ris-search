import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { computed, ref } from "vue";
import type { AnyDocument, SearchResult } from "~/types/api";
import { DocumentKind } from "~/types/api";
import RecentUpdates from "./RecentUpdates.vue";

// The section renders whatever the search returns through SearchResult, so a
// single realistic legislation document is enough to cover every tab.
function result(name: string): SearchResult<AnyDocument> {
  return {
    item: {
      "@type": "Legislation",
      "@id": `eli/bund/bgbl-1/2026/${name}/regelungstext-1`,
      name,
      abbreviation: "TG",
      risAbbreviation: "",
      legislationIdentifier: `eli/bund/bgbl-1/2026/${name}/regelungstext-1`,
      exampleOfWork: {
        "@type": "Legislation",
        "@id": `/v1/legislation/eli/bund/bgbl-1/2026/${name}`,
        legislationIdentifier: `eli/bund/bgbl-1/2026/${name}`,
        legislationDate: "2026-08-12",
        datePublished: "2026-08-12",
      },
      hasPart: [],
      encoding: [],
      legislationLegalForce: "InForce",
      temporalCoverage: "2026-08-12/..",
    },
    textMatches: [],
  } as unknown as SearchResult<AnyDocument>;
}

type Search = {
  documentKind: DocumentKind;
  results?: SearchResult<AnyDocument>[];
  error?: Error;
};

function mockSearches(searches: Search[]) {
  useRecentUpdatesMock.mockResolvedValue(
    searches.map(({ documentKind, results = [], error }) => ({
      documentKind,
      searchResults: computed(() => results),
      searchError: ref(error ?? null),
    })),
  );
}

const defaultSearches: Search[] = [
  { documentKind: DocumentKind.Norm, results: [result("Luftverkehrsgesetz")] },
  { documentKind: DocumentKind.CaseLaw, results: [result("Ein Urteil")] },
  {
    documentKind: DocumentKind.AdministrativeDirective,
    results: [result("Eine Vorschrift")],
  },
  {
    documentKind: DocumentKind.Literature,
    results: [result("Ein Aufsatz")],
  },
];

const { useRecentUpdatesMock } = vi.hoisted(() => ({
  useRecentUpdatesMock: vi.fn(),
}));

mockNuxtImport("useRecentUpdates", () => useRecentUpdatesMock);

mockNuxtImport("useRoute", () => () => ({ fullPath: "/startseite-v2" }));

const renderComponent = () =>
  renderSuspended(RecentUpdates, {
    global: {
      stubs: {
        NuxtLink: {
          props: ["to"],
          template:
            '<a :href="to.path ?? to.name" :data-document-kind="to.query?.documentKind"><slot /></a>',
        },
      },
    },
  });

describe("RecentUpdates", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockSearches(defaultSearches);
  });

  it("labels the tab list", async () => {
    await renderComponent();

    expect(screen.getByRole("tablist")).toHaveAccessibleName(
      "Aktuelles nach Dokumentart",
    );
  });

  it("renders one tab per document kind", async () => {
    await renderComponent();

    expect(
      screen.getAllByRole("tab").map((tab) => tab.textContent?.trim()),
    ).toEqual([
      "Gesetze und Verordnungen",
      "Gerichtsentscheidungen",
      "Verwaltungsvorschriften",
      "Literaturnachweise",
    ]);
  });

  it("selects the first tab by default", async () => {
    await renderComponent();

    const tabs = screen.getAllByRole("tab");
    expect(tabs[0]).toHaveAttribute("aria-selected", "true");
    for (const tab of tabs.slice(1)) {
      expect(tab).toHaveAttribute("aria-selected", "false");
    }
  });

  it("labels the tab panel with the selected tab", async () => {
    await renderComponent();

    const selectedTab = screen.getAllByRole("tab")[0]!;
    expect(screen.getByRole("tabpanel")).toHaveAttribute(
      "aria-labelledby",
      selectedTab.id,
    );
  });

  it("shows the results of the selected document kind", async () => {
    await renderComponent();

    expect(screen.getByText("Luftverkehrsgesetz")).toBeInTheDocument();
    expect(screen.queryByText("Ein Urteil")).not.toBeInTheDocument();
  });

  it("renders result titles as level 3 headings", async () => {
    await renderComponent();

    expect(
      screen.getByRole("heading", { level: 3, name: "Luftverkehrsgesetz" }),
    ).toBeInTheDocument();
  });

  it("links to the simple search for the selected document kind", async () => {
    await renderComponent();

    expect(
      screen.getByRole("link", { name: "Zu den Gesetzen und Verordnungen" }),
    ).toHaveAttribute("data-document-kind", DocumentKind.Norm);
  });

  it("swaps results and button label when another tab is selected", async () => {
    await renderComponent();

    await userEvent.click(
      screen.getByRole("tab", { name: "Literaturnachweise" }),
    );

    expect(screen.getByText("Ein Aufsatz")).toBeInTheDocument();
    expect(screen.queryByText("Luftverkehrsgesetz")).not.toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: "Zu den Literaturnachweisen" }),
    ).toHaveAttribute("data-document-kind", DocumentKind.Literature);
    expect(
      screen.getByRole("tab", { name: "Literaturnachweise" }),
    ).toHaveAttribute("aria-selected", "true");
  });

  it("shows an error on the failing tab only", async () => {
    mockSearches([
      { documentKind: DocumentKind.Norm, error: new Error("Boom") },
      ...defaultSearches.slice(1),
    ]);

    await renderComponent();

    expect(screen.getByRole("alert")).toHaveTextContent(
      "Die Ergebnisse konnten nicht geladen werden.",
    );

    await userEvent.click(
      screen.getByRole("tab", { name: "Gerichtsentscheidungen" }),
    );

    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
    expect(screen.getByText("Ein Urteil")).toBeInTheDocument();
  });

  it("shows a notice when a tab has no results", async () => {
    mockSearches([
      { documentKind: DocumentKind.Norm, results: [] },
      ...defaultSearches.slice(1),
    ]);

    await renderComponent();

    expect(
      screen.getByText("Derzeit keine Einträge verfügbar."),
    ).toBeInTheDocument();
    expect(screen.queryByRole("list")).not.toBeInTheDocument();
  });
});
