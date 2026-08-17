import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SearchResultHeader, {
  type SearchResultHeaderItem,
} from "~/components/search/SearchResultHeader.vue";

function renderComponent(
  items: SearchResultHeaderItem[] = [],
  secondaryItem?: SearchResultHeaderItem,
) {
  return render(SearchResultHeader, {
    props: {
      documentType: { value: "DocumentType", id: "DocTypeID" },
      items: items,
      secondaryItem,
    },
  });
}

describe("SearchResultHeader", () => {
  it("renders documentType", async () => {
    renderComponent();

    const docTypeSpan = screen.getByText("DocumentType");
    expect(docTypeSpan).toBeVisible();
    expect(docTypeSpan).toHaveAttribute("id", "DocTypeID");
    expect(docTypeSpan).toHaveClass(/bold/);
  });

  it("does not render documentType when undefined", async () => {
    render(SearchResultHeader, {
      props: {
        items: [],
      },
    });

    expect(screen.queryByText("DocumentType")).not.toBeInTheDocument();
  });

  it("renders plain text items", async () => {
    renderComponent([{ value: "Item 1" }, { value: "<mark>Item 2</mark>" }]);

    expect(screen.getByText("DocumentType")).toBeVisible();
    expect(screen.getByText("Item 1")).toBeVisible();
    expect(screen.getByText("<mark>Item 2</mark>")).toBeVisible();
  });

  it("renders items as markup", async () => {
    const { container } = renderComponent([
      { value: "Item 1" },
      { isMarkup: true, value: "<mark>Item 2</mark>" },
    ]);

    expect(screen.getByText("DocumentType")).toBeVisible();
    expect(screen.getByText("Item 1")).toBeVisible();
    expect(screen.getByText("Item 2")).toBeVisible();

    const markupSpan = container.querySelector("span:has(mark)");
    expect(markupSpan?.innerHTML).toContain("<mark>Item 2</mark>");
  });

  it("renders trailing component", async () => {
    render(SearchResultHeader, {
      props: {
        documentType: { value: "DocumentType" },
        items: [],
      },
      slots: {
        trailing: "<span>trailing-component</span>",
      },
    });

    expect(screen.getByText("DocumentType")).toBeVisible();
    expect(screen.getByText("trailing-component")).toBeVisible();
  });

  it("renders IDs", async () => {
    renderComponent([
      {
        value: "Item 1",
        id: "foo",
      },
    ]);

    expect(screen.getByText("Item 1")).toHaveAttribute("id", "foo");
  });

  it("does not render empty items", async () => {
    const { container } = renderComponent([
      { value: "Item 1" },
      { value: "" },
      { value: "Item 2" },
    ]);

    expect(screen.getByText("Item 1")).toBeVisible();
    expect(screen.getByText("Item 2")).toBeVisible();

    const itemSpans = container.querySelectorAll("p > span");
    expect(itemSpans).toHaveLength(3); // Icon + 2 text elements
  });

  it("renders an optional secondary row", async () => {
    renderComponent([], { value: "Secondary item" });

    expect(screen.getByText("Secondary item")).toBeVisible();
  });

  it("renders secondary row as markup", async () => {
    const { container } = renderComponent([], {
      value: "<mark>Secondary item</mark>",
      isMarkup: true,
    });

    expect(screen.getByText("Secondary item")).toBeVisible();

    const markupSpan = container.querySelector("p:has(mark)");
    expect(markupSpan?.innerHTML).toContain("<mark>Secondary item</mark>");
  });

  it("does not render secondary row when value is empty", async () => {
    const { container } = renderComponent([], { value: "" });

    const secondaryRows = container.querySelectorAll(".typo-label1-regular");
    expect(secondaryRows).toHaveLength(0);
  });
});
