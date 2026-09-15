import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, within } from "@testing-library/vue";
import { beforeEach, describe, expect, it } from "vitest";
import type { RouteLocationRaw } from "vue-router";
import type { LegislationExpressionPartSchema } from "~/types/api";
import { tocItemsToTreeViewItems } from "~/utils/tableOfContents";
import TableOfContents from "./TableOfContents.vue";

const mockTocItems: LegislationExpressionPartSchema[] = [
  {
    "@id": "chapter1",
    eId: "chapter1",
    name: "1",
    headline: "Chapter 1",
    encoding: [],
    temporalCoverage: "../..",
    hasPart: [
      {
        "@id": "section1-1",
        eId: "section1-1",
        name: "1.1",
        headline: "Section 1.1",
        encoding: [],
        temporalCoverage: "../..",
        hasPart: [],
      },
      {
        "@id": "section1-2",
        eId: "section1-2",
        name: "1.2",
        headline: "Section 1.2",
        encoding: [],
        temporalCoverage: "../..",
        hasPart: [
          {
            "@id": "subsection1-2-1",
            eId: "subsection1-2-1",
            name: "1.2.1",
            headline: "Subsection 1.2.1",
            encoding: [],
            temporalCoverage: "../..",
            hasPart: [],
          },
        ],
      },
    ],
  },
  {
    "@id": "chapter2",
    eId: "chapter2",
    name: "2",
    headline: "Chapter 2",
    encoding: [],
    temporalCoverage: "../..",
    hasPart: [
      {
        "@id": "section2-1",
        eId: "section2-1",
        name: "2.1",
        headline: "Section 2.1",
        encoding: [],
        temporalCoverage: "../..",
        hasPart: [],
      },
    ],
  },
];

const createItems = (items = mockTocItems) =>
  tocItemsToTreeViewItems(
    items,
    (id) => ({ path: "/", hash: `#${id}` }),
    (id) => ({ path: "/ueber", hash: `#${id}` }),
  );

async function renderComponent(props?: {
  selectedKey?: string;
  items?: ReturnType<typeof createItems>;
  selectionEnabled?: boolean;
  subheading?: string;
  subheadingTo?: RouteLocationRaw;
  subheadingAddition?: string;
}) {
  return renderSuspended(TableOfContents, {
    props: {
      tableOfContents: props?.items ?? createItems(),
      selectedKey: props?.selectedKey,
      subheading: props?.subheading,
      subheadingTo: props?.subheadingTo,
      subheadingAddition: props?.subheadingAddition,
    },
  });
}

describe("TableOfContents", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders the root entries and aria label", async () => {
    await renderComponent();

    expect(
      screen.getByRole("tree", {
        name: "Inhalte",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("treeitem", { name: "1, Chapter 1" }),
    ).toBeVisible();
    expect(
      screen.getByRole("treeitem", { name: "2, Chapter 2" }),
    ).toBeVisible();
  });

  it("renders the navigation subtitle", async () => {
    await renderComponent({ subheading: "Norm abbreviation" });

    // Exactly one on desktop and one on mobile
    expect(screen.getAllByText("Norm abbreviation")).toHaveLength(2);
  });

  it("renders the subtitle addition", async () => {
    await renderComponent({
      subheading: "subheading",
      subheadingAddition: "title addition",
    });

    expect(screen.getByText("subheading title addition")).toBeVisible();
  });

  it("does not render the subtitle addition if no subheading is given", async () => {
    await renderComponent({
      subheading: undefined,
      subheadingAddition: "title addition",
    });

    expect(screen.queryByText(/title addition/)).not.toBeInTheDocument();
  });

  it("does not expand any nodes by default", async () => {
    await renderComponent();

    expect(
      screen.queryByRole("treeitem", { name: "1.1, Section 1.1" }),
    ).not.toBeInTheDocument();
  });

  it("expands the selected path and selects the target item", async () => {
    await renderComponent({ selectedKey: "subsection1-2-1" });

    expect(
      screen.getByRole("treeitem", { name: "1.2, Section 1.2" }),
    ).toHaveAttribute("aria-expanded", "true");
    expect(
      screen.getByRole("treeitem", { name: "1.2.1, Subsection 1.2.1" }),
    ).toHaveAttribute("aria-selected", "true");
  });

  it("toggles a node open and closed via the expand button", async () => {
    const user = userEvent.setup();
    await renderComponent();

    const expandButtons = screen.getAllByRole("button", {
      name: "Ebene öffnen",
    });

    await user.click(expandButtons[0]!);
    expect(
      screen.getByRole("treeitem", { name: "1.1, Section 1.1" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Ebene schließen" }));
    expect(
      screen.queryByRole("treeitem", { name: "1.1, Section 1.1" }),
    ).not.toBeInTheDocument();
  });

  it("shows the floating button before the drawer is opened", async () => {
    await renderComponent({ subheading: "Test Norm" });

    const openButton = screen.getByRole("button", {
      name: "Inhalte Test Norm",
    });
    expect(openButton).toBeVisible();
  });

  it("opens the mobile drawer when the floating button is clicked", async () => {
    const user = userEvent.setup();
    await renderComponent({ subheading: "Test Norm" });

    await user.click(screen.getByRole("button", { name: "Inhalte Test Norm" }));

    const dialog = screen.getByRole("dialog", { name: "Inhalte" });
    expect(dialog).toBeVisible();
  });

  it("does not render a link to the expression inside the mobile drawer when subheadingTo is missing", async () => {
    const user = userEvent.setup();
    await renderComponent({ subheading: "Test Norm" });

    await user.click(screen.getByRole("button", { name: "Inhalte Test Norm" }));

    const dialog = screen.getByRole("dialog", { name: "Inhalte" });
    expect(
      within(dialog).queryByRole("link", { name: /Zur Gesamtausgabe/ }),
    ).not.toBeInTheDocument();
  });

  it("renders a link to the expression inside the mobile drawer", async () => {
    const user = userEvent.setup();
    const expressionRouteLocation = "/expressionLink";
    await renderComponent({
      subheading: "Test Norm",
      subheadingTo: expressionRouteLocation,
    });

    await user.click(screen.getByRole("button", { name: "Inhalte Test Norm" }));

    const dialog = screen.getByRole("dialog", { name: "Inhalte" });
    const expressionLink = within(dialog).getByRole("link", {
      name: /Zur Gesamtausgabe/,
    });
    expect(expressionLink).toBeVisible();
    expect(expressionLink).toHaveAttribute("href", expressionRouteLocation);
  });

  it("renders a TOC tree inside the mobile drawer", async () => {
    const user = userEvent.setup();
    await renderComponent({ subheading: "Test Norm" });

    await user.click(screen.getByRole("button", { name: "Inhalte Test Norm" }));

    const dialog = screen.getByRole("dialog", { name: "Inhalte" });
    expect(within(dialog).getByRole("tree", { name: "Inhalte" })).toBeVisible();
  });

  it("keeps the desktop TOC hidden on mobile and visible on desktop", async () => {
    const { container } = await renderComponent();

    // The desktop TreeView has the hidden md:block class
    const desktopToc = container.querySelector(String.raw`.hidden.md\:block`);
    expect(desktopToc).toBeInTheDocument();
  });
});
