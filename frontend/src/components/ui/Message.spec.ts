import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import { h } from "vue";
import Message from "./Message.vue";

const iconFor = (severity: "info" | "warn") =>
  render(Message, {
    props: { severity },
    slots: { default: () => "x" },
  }).container.querySelector("svg")?.innerHTML;

describe("Message", () => {
  it("renders default slot content", () => {
    render(Message, { slots: { default: () => "Etwas ist schiefgelaufen" } });

    expect(screen.getByText("Etwas ist schiefgelaufen")).toBeInTheDocument();
  });

  it("renders rich default slot content", () => {
    render(Message, {
      slots: { default: () => [h("p", "Titel"), h("p", "Beschreibung")] },
    });

    expect(screen.getByText("Titel")).toBeInTheDocument();
    expect(screen.getByText("Beschreibung")).toBeInTheDocument();
  });

  it("renders a decorative default icon", () => {
    const { container } = render(Message, { slots: { default: () => "x" } });

    const icon = container.querySelector("svg");
    expect(icon).toBeInTheDocument();
    expect(icon).toHaveAttribute("aria-hidden", "true");
  });

  it.each(["success", "info", "warn", "error"] as const)(
    "renders a default icon for the %s severity",
    (severity) => {
      const { container } = render(Message, {
        props: { severity },
        slots: { default: () => "x" },
      });

      expect(container.querySelectorAll("svg")).toHaveLength(1);
    },
  );

  it("renders a different default icon per severity", () => {
    expect(iconFor("info")).not.toEqual(iconFor("warn"));
  });

  it("swaps the default icon when the severity changes", async () => {
    const { container, rerender } = render(Message, {
      props: { severity: "info" as const },
      slots: { default: () => "x" },
    });

    const asInfo = container.querySelector("svg")?.innerHTML;

    await rerender({ severity: "warn" });

    expect(container.querySelector("svg")?.innerHTML).not.toEqual(asInfo);
  });

  it("replaces the default icon with the icon slot", () => {
    const { container } = render(Message, {
      slots: {
        default: () => "x",
        icon: () => h("span", { "data-testid": "custom-icon" }, "!"),
      },
    });

    expect(screen.getByTestId("custom-icon")).toBeInTheDocument();
    expect(container.querySelector("svg")).not.toBeInTheDocument();
  });

  it("sets no role or live region by default", () => {
    const { container } = render(Message, { slots: { default: () => "x" } });

    const root = container.firstElementChild;
    expect(root).not.toHaveAttribute("role");
    expect(root).not.toHaveAttribute("aria-live");
    expect(root).not.toHaveAttribute("aria-atomic");
  });

  it("lets a call site opt into a status live region", () => {
    render(Message, {
      attrs: { role: "status" },
      slots: {
        default: () => "Dieser Service befindet sich in der Testphase.",
      },
    });

    expect(screen.getByRole("status")).toHaveTextContent(
      "Dieser Service befindet sich in der Testphase.",
    );
  });

  it("lets a call site opt into an alert live region", () => {
    render(Message, {
      props: { severity: "error" },
      attrs: { role: "alert" },
      slots: { default: () => "Die Suche ist fehlgeschlagen." },
    });

    expect(screen.getByRole("alert")).toHaveTextContent(
      "Die Suche ist fehlgeschlagen.",
    );
  });

  it("applies a passed-through class to the root", () => {
    const { container } = render(Message, {
      attrs: { class: "my-24" },
      slots: { default: () => "x" },
    });

    expect(container.firstElementChild).toHaveClass("my-24");
  });
});
