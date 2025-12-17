import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SearchResultHeader, {
  type SearchResultHeaderItem,
} from "~/components/search/SearchResultHeader.vue";

function renderComponent(items: SearchResultHeaderItem[] = []) {
  return render(SearchResultHeader, {
    props: {
      icon: markRaw({
        template: "<span>icon-stub</span>",
      }),
      items: items,
    },
  });
}

describe("SearchResultHeader", () => {
  it("renders icon", async () => {
    renderComponent();

    expect(screen.getByText("icon-stub")).toBeVisible();
  });

  it("renders plain text items", async () => {
    renderComponent([{ value: "Item 1" }, { value: "<mark>Item 2</mark>" }]);

    expect(screen.getByText("icon-stub")).toBeVisible();
    expect(screen.getByText("Item 1")).toBeVisible();
    expect(screen.getByText("<mark>Item 2</mark>")).toBeVisible();
  });

  it("renders items as markup", async () => {
    const { container } = renderComponent([
      { value: "Item 1" },
      { isMarkup: true, value: "<mark>Item 2</mark>" },
    ]);

    expect(screen.getByText("icon-stub")).toBeVisible();
    expect(screen.getByText("Item 1")).toBeVisible();
    expect(screen.getByText("Item 2")).toBeVisible();

    const markupSpan = container.querySelector("span:has(mark)");
    expect(markupSpan?.innerHTML).toContain("<mark>Item 2</mark>");
  });

  it("renders trailing component", async () => {
    render(SearchResultHeader, {
      props: {
        icon: markRaw({
          template: "<span>icon-stub</span>",
        }),
        items: [],
      },
      slots: {
        trailing: "<span>trailing-component</span>",
      },
    });

    expect(screen.getByText("icon-stub")).toBeVisible();
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
});
