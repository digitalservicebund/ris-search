import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import { h } from "vue";
import Breadcrumb from "./Breadcrumb.vue";

describe("Breadcrumb", () => {
  it("renders each item's label", () => {
    render(Breadcrumb, {
      props: { model: [{ label: "Start" }, { label: "Aktuelle Seite" }] },
    });

    expect(screen.getByText("Start")).toBeInTheDocument();
    expect(screen.getByText("Aktuelle Seite")).toBeInTheDocument();
  });

  it("renders a default separator between items but not after the last one", () => {
    const { container } = render(Breadcrumb, {
      props: { model: [{ label: "A" }, { label: "B" }, { label: "C" }] },
    });

    const separators = container.querySelectorAll('li[aria-hidden="true"]');
    expect(separators).toHaveLength(2);
    separators.forEach((separator) => expect(separator).toHaveTextContent("/"));
  });

  it("renders a custom separator slot instead of the default one", () => {
    const { container } = render(Breadcrumb, {
      props: { model: [{ label: "A" }, { label: "B" }] },
      slots: { separator: () => "→" },
    });

    const separators = container.querySelectorAll('li[aria-hidden="true"]');
    expect(separators).toHaveLength(1);
    expect(separators[0]).toHaveTextContent("→");
  });

  it("passes item and index to the scoped item slot", () => {
    render(Breadcrumb, {
      props: { model: [{ label: "A" }, { label: "B" }] },
      slots: {
        item: (scope: { item: { label: string }; index: number }) =>
          h("span", { "data-testid": `item-${scope.index}` }, scope.item.label),
      },
    });

    expect(screen.getByTestId("item-0")).toHaveTextContent("A");
    expect(screen.getByTestId("item-1")).toHaveTextContent("B");
  });

  it("falls through aria-label and class to the root nav element", () => {
    render(Breadcrumb, {
      props: { model: [{ label: "A" }] },
      attrs: { "aria-label": "Pfadnavigation", class: "custom-class" },
    });

    const nav = screen.getByRole("navigation", { name: "Pfadnavigation" });
    expect(nav).toHaveClass("custom-class");
  });
});
