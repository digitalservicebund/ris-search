import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import Accordion from "./Accordion.vue";

const headers = {
  headerCollapsed: "Show More",
  headerExpanded: "Show Less",
};

describe("Accordion", () => {
  it("shows the collapsed header when closed", () => {
    render(Accordion, {
      props: headers,
      slots: { default: '<div class="slot-content">Slot Content</div>' },
    });

    expect(screen.getByRole("button")).toHaveTextContent("Show More");
  });

  it("shows the expanded header when open", () => {
    render(Accordion, { props: { ...headers, modelValue: true } });

    expect(screen.getByRole("button")).toHaveTextContent("Show Less");
  });

  it("keeps slot content in the DOM but hidden while closed", () => {
    render(Accordion, {
      props: headers,
      slots: { default: "<div>Slot Content</div>" },
    });

    expect(screen.getByText("Slot Content")).toBeInTheDocument();
    expect(screen.getByText("Slot Content")).not.toBeVisible();
  });

  it("shows slot content while open", () => {
    render(Accordion, {
      props: { ...headers, modelValue: true },
      slots: { default: "<div>Slot Content</div>" },
    });

    expect(screen.getByText("Slot Content")).toBeVisible();
  });

  it("emits the model update when opened", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Accordion, {
      props: { ...headers, modelValue: false },
    });

    await user.click(screen.getByRole("button"));

    expect(emitted("update:modelValue")).toContainEqual([true]);
  });

  it("emits the model update when closed", async () => {
    const user = userEvent.setup();
    const { emitted } = render(Accordion, {
      props: { ...headers, modelValue: true },
    });

    await user.click(screen.getByRole("button"));

    expect(emitted("update:modelValue")).toContainEqual([false]);
  });

  it("responds to external model changes", async () => {
    const { rerender } = render(Accordion, {
      props: { ...headers, modelValue: false },
    });

    expect(screen.getByRole("button")).toHaveTextContent("Show More");

    await rerender({ modelValue: true });
    expect(screen.getByRole("button")).toHaveTextContent("Show Less");

    await rerender({ modelValue: false });
    expect(screen.getByRole("button")).toHaveTextContent("Show More");
  });

  it("reflects the open state via aria-expanded", async () => {
    const { rerender } = render(Accordion, {
      props: { ...headers, modelValue: false },
    });

    expect(screen.getByRole("button")).toHaveAttribute(
      "aria-expanded",
      "false",
    );

    await rerender({ modelValue: true });

    expect(screen.getByRole("button")).toHaveAttribute("aria-expanded", "true");
  });

  it("names the content region with the expanded header", () => {
    render(Accordion, {
      props: { ...headers, modelValue: true },
      slots: { default: "<div>Slot Content</div>" },
    });

    expect(
      screen.getByRole("region", { name: "Show Less" }),
    ).toBeInTheDocument();
  });

  it("removes the content region from the accessibility tree while closed", () => {
    render(Accordion, {
      props: { ...headers, modelValue: false },
      slots: { default: "<div>Slot Content</div>" },
    });

    expect(screen.queryByRole("region")).not.toBeInTheDocument();
  });

  // Activating the header from the keyboard is native summary behaviour: browsers
  // dispatch a click for Enter and Space, which the click handler already covers.
  // jsdom does not implement that, so this asserts the part we control - the
  // header is reachable by keyboard - and the toggle itself is covered by the
  // click tests above and by the e2e suite.
  it("exposes a focusable header", () => {
    render(Accordion, { props: headers });

    const header = screen.getByRole("button");
    header.focus();

    expect(header).toHaveFocus();
  });

  it("applies a passed-through class to the root", () => {
    const { container } = render(Accordion, {
      props: headers,
      attrs: { class: "print:hidden" },
    });

    expect(container.firstElementChild).toHaveClass("print:hidden");
  });
});
