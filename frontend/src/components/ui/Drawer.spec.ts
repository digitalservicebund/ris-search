import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import Drawer from "./Drawer.vue";

describe("Drawer", () => {
  it("does not show the dialog when visible is false", () => {
    render(Drawer, { props: { visible: false, header: "Titel" } });

    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  });

  it("shows the dialog when visible is true", () => {
    render(Drawer, { props: { visible: true, header: "Titel" } });

    expect(screen.getByRole("dialog")).toBeVisible();
  });

  it("opens when visible changes from false to true", async () => {
    const { rerender } = render(Drawer, {
      props: { visible: false, header: "Titel" },
    });

    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();

    await rerender({ visible: true, header: "Titel" });

    expect(screen.getByRole("dialog")).toBeVisible();
  });

  it("renders the header prop", () => {
    render(Drawer, { props: { visible: true, header: "Filtern" } });

    expect(screen.getByText("Filtern")).toBeVisible();
  });

  it("renders the header slot instead of the header prop", () => {
    render(Drawer, {
      props: { visible: true, header: "Filtern" },
      slots: { header: "<span>Custom header</span>" },
    });

    expect(screen.getByText("Custom header")).toBeVisible();
    expect(screen.queryByText("Filtern")).not.toBeInTheDocument();
  });

  it("renders the default slot content", () => {
    render(Drawer, {
      props: { visible: true },
      slots: { default: "<p>Body content</p>" },
    });

    expect(screen.getByText("Body content")).toBeVisible();
  });

  it("renders the footer slot when given", () => {
    render(Drawer, {
      props: { visible: true },
      slots: { footer: "<p>Footer content</p>" },
    });

    expect(screen.getByText("Footer content")).toBeVisible();
  });

  it("passes through native attributes to the dialog element", () => {
    render(Drawer, {
      props: { visible: true },
      attrs: { id: "my-drawer", "aria-label": "Filtern" },
    });

    const dialog = screen.getByRole("dialog", { name: "Filtern" });
    expect(dialog).toHaveAttribute("id", "my-drawer");
  });

  it("emits update:visible false when the close button is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Drawer, {
      props: { visible: true, header: "Filtern" },
    });

    await user.click(screen.getByRole("button", { name: "Schließen" }));

    expect(emitted("update:visible")).toEqual([[false]]);
  });

  it("emits update:visible false when clicking the backdrop (dialog element itself)", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Drawer, {
      props: { visible: true, header: "Filtern" },
    });

    await user.click(screen.getByRole("dialog"));

    expect(emitted("update:visible")).toEqual([[false]]);
  });

  it("does not close when clicking inside the content", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Drawer, {
      props: { visible: true, header: "Filtern" },
      slots: { default: "<p>Body content</p>" },
    });

    await user.click(screen.getByText("Body content"));

    expect(emitted("update:visible")).toBeUndefined();
  });

  it("emits update:visible false when Escape is pressed", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Drawer, {
      props: { visible: true, header: "Filtern" },
    });

    screen.getByRole("button", { name: "Schließen" }).focus();
    await user.keyboard("{Escape}");

    expect(emitted("update:visible")).toEqual([[false]]);
  });

  it("emits update:visible false when the dialog fires a native close event", () => {
    const { emitted } = render(Drawer, {
      props: { visible: true, header: "Filtern" },
    });

    screen.getByRole("dialog").dispatchEvent(new Event("close"));

    expect(emitted("update:visible")).toEqual([[false]]);
  });
});
