import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import SearchResultHeader, {
  type SearchResultHeaderItem,
} from "~/components/Search/SearchResultHeader.vue";

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
    renderComponent([
      {
        value: "Item 1",
      },
      {
        value: "<mark>Item 2</mark>",
      },
    ]);
    expect(screen.getByText("icon-stub")).toBeVisible();
    expect(screen.getByText("icon-stub").nextElementSibling).toHaveTextContent(
      "Item 1",
    );
    // Is not rendered as markup
    expect(screen.getByText("Item 1").nextElementSibling).toHaveTextContent(
      "<mark>Item 2</mark>",
    );
  });

  it("renders items as markup", async () => {
    renderComponent([
      {
        value: "Item 1",
      },
      {
        isMarkup: true,
        value: "<mark>Item 2</mark>",
      },
    ]);
    expect(screen.getByText("icon-stub")).toBeVisible();
    expect(screen.getByText("icon-stub").nextElementSibling).toHaveTextContent(
      "Item 1",
    );
    expect(screen.getByText("Item 1").nextElementSibling?.innerHTML).toContain(
      "<mark>Item 2</mark>",
    );
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
    expect(screen.getByText("icon-stub").nextElementSibling).toHaveTextContent(
      "trailing-component",
    );
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
});
