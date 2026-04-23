import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen, within } from "@testing-library/vue";
import { userEvent } from "@testing-library/user-event";
import { beforeEach, describe, expect, it } from "vitest";
import TableOfContents from "./TableOfContents.vue";
import type { LegislationExpressionPartSchema } from "~/types/api";
import { tocItemsToTreeViewItems } from "~/utils/tableOfContents";

describe("TableOfContents", () => {
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
      (id) => ({ path: "/about", hash: `#${id}` }),
    );

  beforeEach(() => {
    vi.clearAllMocks();
  });

  async function renderComponent(props?: {
    selectedKey?: string;
    items?: ReturnType<typeof createItems>;
    selectionEnabled?: boolean;
    subheading?: string;
  }) {
    return renderSuspended(TableOfContents, {
      props: {
        tableOfContents: props?.items ?? createItems(),
        selectedKey: props?.selectedKey,
        subheading: props?.subheading,
      },
    });
  }

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

    expect(screen.getAllByText("Norm abbreviation").length).toBeGreaterThan(0);
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
