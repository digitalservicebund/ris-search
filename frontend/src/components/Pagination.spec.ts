import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import Pagination, { type Page } from "./Pagination.vue";
import type { SearchResult, CaseLaw } from "~/types";

const createMockSearchResult = (): SearchResult<CaseLaw> => ({
  item: {
    "@type": "Decision",
    "@id": "test-id",
    documentNumber: "TEST-123",
    ecli: "ECLI:TEST:123",
    decisionDate: "2024-01-01",
    fileNumbers: [],
    keywords: [],
    decisionName: [],
    deviatingDocumentNumber: [],
    inLanguage: "de",
    encoding: [],
  },
  textMatches: [],
});

const createMockPage = (overrides?: Partial<Page>): Page => ({
  member: [createMockSearchResult(), createMockSearchResult()],
  "@id": "/api/search?pageIndex=0&size=10",
  totalItems: 100,
  view: {
    first: "/api/search?pageIndex=0&size=10",
    next: "/api/search?pageIndex=1&size=10",
    last: "/api/search?pageIndex=9&size=10",
  },
  ...overrides,
});

describe("Pagination", () => {
  it("renders nothing when page is null", async () => {
    const { container } = await renderSuspended(Pagination, {
      props: { page: null },
    });

    expect(container.querySelector("nav")).not.toBeInTheDocument();
  });

  it("renders nothing when page has no members", async () => {
    const page = createMockPage({ member: [] });
    const { container } = await renderSuspended(Pagination, {
      props: { page },
    });

    expect(container.querySelector("nav")).not.toBeInTheDocument();
  });

  it("renders nothing when isLoading is true", async () => {
    const page = createMockPage();
    const { container } = await renderSuspended(Pagination, {
      props: { page, isLoading: true },
    });

    expect(container.querySelector("nav")).not.toBeInTheDocument();
  });

  it("renders page information with items on page", async () => {
    const page = createMockPage();
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(screen.getByText(/Seite 1:/)).toBeInTheDocument();
    expect(screen.getByText(/Treffer 1–2 von 100/)).toBeInTheDocument();
  });

  it("renders only items on page when it's the only page", async () => {
    const page = createMockPage({
      member: [createMockSearchResult()],
      totalItems: 1,
      view: {
        first: "/api/search?pageIndex=0&size=10",
      },
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(screen.queryByLabelText("Zurück")).not.toBeInTheDocument();
    expect(screen.queryByLabelText("Weiter")).not.toBeInTheDocument();
    expect(screen.queryByText(/Seite 1:/)).not.toBeInTheDocument();
    expect(screen.getByText(/Treffer 1 von 1/)).toBeInTheDocument();
  });

  it("renders navigation buttons when there are multiple pages", async () => {
    const page = createMockPage();
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(screen.getByLabelText("vorherige Ergebnisse")).toBeInTheDocument();
    expect(screen.getByLabelText("nächste Ergebnisse")).toBeInTheDocument();
  });

  it("disables previous button on first page", async () => {
    const page = createMockPage({
      view: {
        first: "/api/search?pageIndex=0&size=10",
        next: "/api/search?pageIndex=1&size=10",
        last: "/api/search?pageIndex=9&size=10",
      },
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(
      screen.getByRole("button", { name: "vorherige Ergebnisse" }),
    ).toBeDisabled();
    expect(
      screen.getByRole("link", { name: "nächste Ergebnisse" }),
    ).toBeInTheDocument();
  });

  it("disables next button on last page", async () => {
    const page = createMockPage({
      "@id": "/api/search?pageIndex=9&size=10",
      view: {
        first: "/api/search?pageIndex=0&size=10",
        previous: "/api/search?pageIndex=8&size=10",
        last: "/api/search?pageIndex=9&size=10",
      },
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(
      screen.getByRole("link", { name: "vorherige Ergebnisse" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "nächste Ergebnisse" }),
    ).toBeDisabled();
  });

  it("next button links to correct page", async () => {
    const page = createMockPage();
    await renderSuspended(Pagination, {
      props: { page },
    });

    const nextLink = screen.getByRole("link", { name: "nächste Ergebnisse" });
    expect(nextLink).toHaveAttribute("href", "/?pageIndex=1");
  });

  it("previous button links to correct page", async () => {
    const page = createMockPage({
      "@id": "/api/search?pageIndex=2&size=10",
      view: {
        first: "/api/search?pageIndex=0&size=10",
        previous: "/api/search?pageIndex=1&size=10",
        next: "/api/search?pageIndex=3&size=10",
        last: "/api/search?pageIndex=9&size=10",
      },
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    const previousLink = screen.getByRole("link", {
      name: "vorherige Ergebnisse",
    });
    expect(previousLink).toHaveAttribute("href", "/?pageIndex=1");
  });

  it("previous button removes pageIndex param when going to first page", async () => {
    const page = createMockPage({
      "@id": "/api/search?pageIndex=1&size=10",
      view: {
        first: "/api/search?pageIndex=0&size=10",
        previous: "/api/search?pageIndex=0&size=10",
        next: "/api/search?pageIndex=2&size=10",
        last: "/api/search?pageIndex=9&size=10",
      },
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    const previousLink = screen.getByRole("link", {
      name: "vorherige Ergebnisse",
    });
    expect(previousLink).toHaveAttribute("href", "/");
  });

  it("displays correct page number for middle page", async () => {
    const page = createMockPage({
      "@id": "/api/search?pageIndex=5&size=10",
      view: {
        first: "/api/search?pageIndex=0&size=10",
        previous: "/api/search?pageIndex=4&size=10",
        next: "/api/search?pageIndex=6&size=10",
        last: "/api/search?pageIndex=9&size=10",
      },
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(screen.getByText(/Seite 6:/)).toBeInTheDocument();
  });

  it("renders navigation at top when navigationPosition is top", async () => {
    const page = createMockPage();
    const { container } = await renderSuspended(Pagination, {
      props: { page, navigationPosition: "top" },
      slots: {
        default: '<div data-testid="slot-content">Slot content</div>',
      },
    });

    const nav = container.querySelector("nav");
    const slotContent = screen.getByTestId("slot-content");

    expect(nav).toBeInTheDocument();
    expect(slotContent).toBeInTheDocument();

    // Nav should come before slot content in DOM
    expect(nav!.compareDocumentPosition(slotContent)).toBe(
      Node.DOCUMENT_POSITION_FOLLOWING,
    );
  });

  it("renders navigation at bottom when navigationPosition is bottom", async () => {
    const page = createMockPage();
    const { container } = await renderSuspended(Pagination, {
      props: { page, navigationPosition: "bottom" },
      slots: {
        default: '<div data-testid="slot-content">Slot content</div>',
      },
    });

    const nav = container.querySelector("nav");
    const slotContent = screen.getByTestId("slot-content");

    expect(nav).toBeInTheDocument();
    expect(slotContent).toBeInTheDocument();

    // Slot content should come before nav in DOM
    expect(slotContent.compareDocumentPosition(nav!)).toBe(
      Node.DOCUMENT_POSITION_FOLLOWING,
    );
  });

  it("handles pages with more than 10,000 results", async () => {
    const page = createMockPage({
      totalItems: 10000,
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(
      screen.getByText(/Treffer 1–2 von mehr als 10\.000/),
    ).toBeInTheDocument();
  });

  it("formats large item counts with German locale", async () => {
    const page = createMockPage({
      "@id": "/api/search?pageIndex=0&size=10",
      member: Array.from({ length: 10 }, () => createMockSearchResult()),
      totalItems: 1234,
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(screen.getByText(/Treffer 1–10 von 1\.234/)).toBeInTheDocument();
  });

  it("renders navigation with proper aria-label", async () => {
    const page = createMockPage();
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(
      screen.getByRole("navigation", { name: "Paginierung" }),
    ).toBeInTheDocument();
  });

  it("handles single item on page correctly", async () => {
    const page = createMockPage({
      "@id": "/api/search?pageIndex=2&size=10",
      member: [createMockSearchResult()],
      totalItems: 100,
      view: {
        first: "/api/search?pageIndex=0&size=10",
        previous: "/api/search?pageIndex=1&size=10",
        next: "/api/search?pageIndex=3&size=10",
        last: "/api/search?pageIndex=9&size=10",
      },
    });
    await renderSuspended(Pagination, {
      props: { page },
    });

    expect(screen.getByText(/Treffer 21 von 100/)).toBeInTheDocument();
  });
});
