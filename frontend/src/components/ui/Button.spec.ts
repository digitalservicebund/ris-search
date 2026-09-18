import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import { h } from "vue";
import Button from "./Button.vue";

describe("Button", () => {
  it("renders the label prop as its accessible name", () => {
    render(Button, { props: { label: "Speichern" } });

    expect(
      screen.getByRole("button", { name: "Speichern" }),
    ).toBeInTheDocument();
  });

  it("renders default slot content", () => {
    render(Button, { slots: { default: () => "Abbrechen" } });

    expect(
      screen.getByRole("button", { name: "Abbrechen" }),
    ).toBeInTheDocument();
  });

  it("renders as a native button with type=button by default", () => {
    render(Button, { props: { label: "x" } });

    expect(screen.getByRole("button")).toHaveAttribute("type", "button");
  });

  it("forwards the type prop", () => {
    render(Button, { props: { label: "x", type: "submit" } });

    expect(screen.getByRole("button")).toHaveAttribute("type", "submit");
  });

  it("renders the icon slot and passes the position class as scope", () => {
    render(Button, {
      props: { label: "x", iconPos: "right" },
      slots: {
        icon: (scope: { class: unknown }) =>
          h("span", { class: scope.class, "data-testid": "icon" }, "i"),
      },
    });

    expect(screen.getByTestId("icon")).toHaveClass("order-last");
  });

  it("shows a spinner and disables the button while loading", () => {
    render(Button, { props: { label: "x", loading: true } });

    expect(screen.getByLabelText("Ladestatus")).toBeInTheDocument();
    expect(screen.getByRole("button")).toBeDisabled();
  });

  it("reflects the disabled prop", () => {
    render(Button, { props: { label: "x", disabled: true } });

    expect(screen.getByRole("button")).toBeDisabled();
  });

  it("emits click events", async () => {
    const user = userEvent.setup();
    const onClick = vi.fn();
    render(Button, { props: { label: "x" }, attrs: { onClick } });

    await user.click(screen.getByRole("button"));

    expect(onClick).toHaveBeenCalledOnce();
  });

  it("passes through native attributes", () => {
    render(Button, { attrs: { "aria-label": "Nur Icon" } });

    expect(
      screen.getByRole("button", { name: "Nur Icon" }),
    ).toBeInTheDocument();
  });

  it("renders as a different element via the as prop", () => {
    render(Button, {
      props: { as: "a" },
      attrs: { href: "#target" },
      slots: { default: () => "Zum Ziel" },
    });

    const link = screen.getByRole("link", { name: "Zum Ziel" });
    expect(link).toHaveAttribute("href", "#target");
    expect(link).not.toHaveAttribute("type");
  });
});
