import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import { h } from "vue";
import Tooltip from "./Tooltip.vue";

const trigger = () => h("button", "Auslöser");

describe("Tooltip", () => {
  it("renders the trigger slot content", () => {
    render(Tooltip, {
      props: { text: "Hinweis" },
      slots: { default: trigger },
    });

    expect(
      screen.getByRole("button", { name: "Auslöser" }),
    ).toBeInTheDocument();
  });

  it("does not show the tooltip text until triggered", () => {
    render(Tooltip, {
      props: { text: "Hinweis" },
      slots: { default: trigger },
    });

    expect(
      screen.queryByRole("tooltip", { hidden: true }),
    ).not.toBeInTheDocument();
  });

  it("shows the tooltip text on focus and hides it on blur", async () => {
    const user = userEvent.setup();
    render(Tooltip, {
      props: { text: "Hinweis" },
      slots: { default: trigger },
    });

    await user.tab();

    expect(screen.getByRole("tooltip", { hidden: true })).toHaveTextContent(
      "Hinweis",
    );

    await user.tab();

    expect(
      screen.queryByRole("tooltip", { hidden: true }),
    ).not.toBeInTheDocument();
  });

  it("marks the trigger as described by the tooltip content while open", async () => {
    const user = userEvent.setup();
    render(Tooltip, {
      props: { text: "Hinweis" },
      slots: { default: trigger },
    });

    const triggerEl = screen.getByRole("button", { name: "Auslöser" });
    expect(triggerEl).not.toHaveAttribute("aria-describedby");

    await user.tab();

    const tooltip = screen.getByRole("tooltip", { hidden: true });
    expect(tooltip).toHaveTextContent("Hinweis");
    expect(triggerEl).toHaveAttribute("aria-describedby", tooltip.id);
  });

  it("does not open when no text is provided", async () => {
    const user = userEvent.setup();
    render(Tooltip, {
      slots: { default: trigger },
    });

    await user.tab();

    expect(
      screen.queryByRole("tooltip", { hidden: true }),
    ).not.toBeInTheDocument();
  });
});
