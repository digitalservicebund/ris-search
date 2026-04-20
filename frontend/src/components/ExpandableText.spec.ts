import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, test, vi } from "vitest";
import ExpandableText from "./ExpandableText.vue";

describe("ExpandableText", () => {
  test("renders the text", async () => {
    await renderSuspended(ExpandableText, { slots: { default: "Test" } });
    expect(screen.getByText("Test")).toBeInTheDocument();
  });

  test("renders an expand button", async () => {
    vi.spyOn(HTMLElement.prototype, "scrollHeight", "get").mockReturnValue(100);
    vi.spyOn(HTMLElement.prototype, "clientHeight", "get").mockReturnValue(50);
    await renderSuspended(ExpandableText, { slots: { default: "Test" } });

    await vi.waitFor(() => {
      expect(
        screen.getByRole("button", { name: "Mehr anzeigen" }),
      ).toBeInTheDocument();
    });
  });

  test("expands the text", async () => {
    vi.spyOn(HTMLElement.prototype, "scrollHeight", "get").mockReturnValue(100);
    vi.spyOn(HTMLElement.prototype, "clientHeight", "get").mockReturnValue(50);
    const user = userEvent.setup();
    await renderSuspended(ExpandableText, {
      slots: { default: "Test" },
      props: { expanded: false },
    });

    await vi.waitFor(() => screen.getByRole("button"));

    await user.click(screen.getByRole("button"));

    expect(screen.getByRole("button")).toHaveAttribute("aria-expanded", "true");
  });

  test("collapses the text", async () => {
    vi.spyOn(HTMLElement.prototype, "scrollHeight", "get").mockReturnValue(100);
    vi.spyOn(HTMLElement.prototype, "clientHeight", "get").mockReturnValue(50);
    const user = userEvent.setup();
    await renderSuspended(ExpandableText, {
      slots: { default: "Test" },
      props: { expanded: true },
    });

    await vi.waitFor(() => screen.getByRole("button"));

    await user.click(screen.getByRole("button"));

    expect(screen.getByRole("button")).toHaveAttribute(
      "aria-expanded",
      "false",
    );
  });

  test("renders a collapse button", async () => {
    vi.spyOn(HTMLElement.prototype, "scrollHeight", "get").mockReturnValue(100);
    vi.spyOn(HTMLElement.prototype, "clientHeight", "get").mockReturnValue(50);
    await renderSuspended(ExpandableText, {
      props: { expanded: true },
      slots: { default: "Test" },
    });

    await vi.waitFor(() => {
      expect(
        screen.getByRole("button", { name: "Weniger anzeigen" }),
      ).toBeInTheDocument();
    });
  });

  test("does not render the expand/collapse button if the text is not truncated", async () => {
    vi.spyOn(HTMLElement.prototype, "scrollHeight", "get").mockReturnValue(100);
    vi.spyOn(HTMLElement.prototype, "clientHeight", "get").mockReturnValue(100);
    await renderSuspended(ExpandableText, { slots: { default: "Test" } });

    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });
});
