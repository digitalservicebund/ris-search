import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import Tab from "./Tab.vue";

describe("Tab", () => {
  it("renders a native button with its content", () => {
    render(Tab, { slots: { default: "Text" } });

    const tab = screen.getByRole("tab", { name: "Text" });

    expect(tab.tagName).toBe("BUTTON");
    expect(tab).toHaveAttribute("type", "button");
  });

  it("is not selected and not tabbable by default", () => {
    render(Tab, { slots: { default: "Text" } });

    const tab = screen.getByRole("tab", { name: "Text" });

    expect(tab).toHaveAttribute("aria-selected", "false");
    expect(tab).toHaveAttribute("tabindex", "-1");
  });

  it("is selected and tabbable when active", () => {
    render(Tab, { props: { active: true }, slots: { default: "Text" } });

    const tab = screen.getByRole("tab", { name: "Text" });

    expect(tab).toHaveAttribute("aria-selected", "true");
    expect(tab).toHaveAttribute("tabindex", "0");
  });

  it("does not reference a tab panel", () => {
    render(Tab, { slots: { default: "Text" } });

    expect(screen.getByRole("tab", { name: "Text" })).not.toHaveAttribute(
      "aria-controls",
    );
  });

  it("renders as another element when `as` is set", () => {
    render(Tab, {
      props: { as: "a" },
      attrs: { href: "/details" },
      slots: { default: "Details" },
    });

    const tab = screen.getByRole("tab", { name: "Details" });

    expect(tab.tagName).toBe("A");
    expect(tab).toHaveAttribute("href", "/details");
    expect(tab).not.toHaveAttribute("type");
  });

  it("passes attributes through", () => {
    render(Tab, {
      attrs: { "data-attr": "text-tab" },
      slots: { default: "Text" },
    });

    expect(screen.getByRole("tab", { name: "Text" })).toHaveAttribute(
      "data-attr",
      "text-tab",
    );
  });

  it("can be clicked", async () => {
    const onClick = vi.fn();

    render(Tab, {
      attrs: { onClick },
      slots: { default: "Text" },
    });

    await userEvent.click(screen.getByRole("tab", { name: "Text" }));

    expect(onClick).toHaveBeenCalledOnce();
  });
});
