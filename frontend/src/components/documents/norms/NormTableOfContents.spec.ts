import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { userEvent } from "@testing-library/user-event";
import { beforeEach, describe, expect, it } from "vitest";
import NormTableOfContents from "./NormTableOfContents.vue";
import type { TableOfContentsItem } from "~/types/api";
import { tocItemsToTreeViewItems } from "~/utils/tableOfContents";

describe("NormTableOfContents", () => {
  const mockTocItems: TableOfContentsItem[] = [
    {
      "@type": "TocEntry",
      id: "chapter1",
      marker: "1",
      heading: "Chapter 1",
      children: [
        {
          "@type": "TocEntry",
          id: "section1-1",
          marker: "1.1",
          heading: "Section 1.1",
          children: [],
        },
        {
          "@type": "TocEntry",
          id: "section1-2",
          marker: "1.2",
          heading: "Section 1.2",
          children: [
            {
              "@type": "TocEntry",
              id: "subsection1-2-1",
              marker: "1.2.1",
              heading: "Subsection 1.2.1",
              children: [],
            },
          ],
        },
      ],
    },
    {
      "@type": "TocEntry",
      id: "chapter2",
      marker: "2",
      heading: "Chapter 2",
      children: [
        {
          "@type": "TocEntry",
          id: "section2-1",
          marker: "2.1",
          heading: "Section 2.1",
          children: [],
        },
      ],
    },
  ];

  const headingBasePath = "/headings/";
  const leafBasePath = "/leaf/";

  const createItems = (items = mockTocItems) =>
    tocItemsToTreeViewItems(items, headingBasePath, leafBasePath);

  beforeEach(() => {
    vi.clearAllMocks();
  });

  async function renderComponent(props?: {
    selectedKey?: string;
    items?: ReturnType<typeof createItems>;
  }) {
    return renderSuspended(NormTableOfContents, {
      props: {
        tableOfContents: props?.items ?? createItems(),
        selectedKey: props?.selectedKey,
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

  it("toggles the mobile drawer", async () => {
    const user = userEvent.setup();
    const { container } = await renderComponent();

    const toc = container.querySelector('[data-testid="table-of-contents"]');
    expect(toc).toHaveAttribute("data-selected", "false");

    await user.click(screen.getByTestId("mobile-toc-button"));
    expect(toc).toHaveAttribute("data-selected", "true");

    await user.click(
      screen.getByRole("button", { name: "Inhaltsverzeichnis schließen" }),
    );
    expect(toc).toHaveAttribute("data-selected", "false");
  });

  it("closes the mobile drawer when an item is clicked", async () => {
    const user = userEvent.setup();
    const { container } = await renderComponent();

    await user.click(screen.getByTestId("mobile-toc-button"));
    await user.click(screen.getByText("1"));

    const toc = container.querySelector('[data-testid="table-of-contents"]');
    expect(toc).toHaveAttribute("data-selected", "false");
  });

  it("keeps the responsive overlay classes on the container", async () => {
    const { container } = await renderComponent();

    const toc = container.querySelector('[data-testid="table-of-contents"]');
    expect(toc).toHaveClass("max-lg:fixed");
    expect(toc).toHaveClass("max-lg:left-0");
  });
});
