import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SearchResultHeader, {
  type SearchResultHeaderItem,
  type TextHeaderItem,
} from "~/components/search/SearchResultHeader.vue";

function renderComponent(
  items: SearchResultHeaderItem[] = [],
  secondaryItem?: TextHeaderItem,
) {
  return render(SearchResultHeader, {
    props: {
      documentType: { type: "text", value: "DocumentType", id: "DocTypeID" },
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
    renderComponent([
      { type: "text", value: "Item 1" },
      { type: "text", value: "<mark>Item 2</mark>" },
    ]);

    expect(screen.getByText("Item 1")).toBeVisible();
    expect(screen.getByText("<mark>Item 2</mark>")).toBeVisible();
  });

  it("renders items as badges", async () => {
    renderComponent([
      { type: "badge", value: "Item 1", color: "green" },
      { type: "badge", value: "Item 2", color: "blue", class: "bold" },
    ]);

    const item1 = screen.getByText("Item 1");
    expect(item1).toBeVisible();
    expect(item1).toHaveClass(/green/);

    const item2 = screen.getByText("Item 2");
    expect(item2).toBeVisible();
    expect(item2).toHaveClass(/blue/, "bold");
  });

  it("renders item as badge with markup", async () => {
    const { container } = renderComponent([
      {
        type: "badge",
        value: "<mark>Item 1</mark>",
        color: "green",
        isMarkup: true,
      },
    ]);

    const markupSpan = container.querySelector("span:has(mark)");
    expect(markupSpan?.innerHTML).toContain("<mark>Item 1</mark>");
    expect(markupSpan).toHaveClass(/green/);
  });

  it("renders IDs", async () => {
    renderComponent([
      {
        type: "text",
        value: "Item 1",
        id: "foo",
      },
      {
        type: "badge",
        value: "Item 2",
        color: "green",
        id: "bar",
      },
    ]);

    expect(screen.getByText("Item 1")).toHaveAttribute("id", "foo");
    expect(screen.getByText("Item 2")).toHaveAttribute("id", "bar");
  });

  it("does not render empty items", async () => {
    const { container } = renderComponent([
      { type: "text", value: "Item 1" },
      { type: "text", value: "" },
      { type: "badge", value: "Item 2", color: "green" },
      { type: "badge", value: "", color: "green" },
      { type: "badge", value: "", color: "green", isMarkup: true },
    ]);

    expect(screen.getByText("Item 1")).toBeVisible();
    expect(screen.getByText("Item 2")).toBeVisible();

    const itemSpans = container.querySelectorAll("p > span");
    expect(itemSpans).toHaveLength(3); // document type + 2 items
  });

  it("renders an optional secondary row", async () => {
    renderComponent([], { type: "text", value: "Secondary item" });

    expect(screen.getByText("Secondary item")).toBeVisible();
  });

  it("does not render secondary row when value is empty", async () => {
    const { container } = renderComponent([], { type: "text", value: "" });

    const secondaryRows = container.querySelectorAll(".typo-label1-regular");
    expect(secondaryRows).toHaveLength(0);
  });
});
