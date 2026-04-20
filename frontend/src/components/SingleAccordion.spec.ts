import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { userEvent } from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import SingleAccordion from "./SingleAccordion.vue";

describe("SingleAccordion", () => {
  it("shows collapsed header and down icon when closed", async () => {
    await renderSuspended(SingleAccordion, {
      props: { headerCollapsed: "Show More", headerExpanded: "Show Less" },
      slots: { default: '<div class="slot-content">Slot Content</div>' },
    });

    expect(screen.getByRole("button")).toHaveTextContent("Show More");
    expect(screen.getByText("Slot Content")).toBeInTheDocument();
  });

  it("shows expanded header and rotated icon when open", async () => {
    await renderSuspended(SingleAccordion, {
      props: {
        headerCollapsed: "Show More",
        headerExpanded: "Show Less",
        modelValue: true,
      },
    });

    expect(screen.getByRole("button")).toHaveTextContent("Show Less");
  });

  it("updates v-model when accordion is opened", async () => {
    const user = userEvent.setup();
    const { rerender } = await renderSuspended(SingleAccordion, {
      props: {
        modelValue: false,
        headerCollapsed: "Collapsed Header",
        headerExpanded: "Expanded Header",
      },
    });

    expect(screen.getByRole("button")).toHaveTextContent("Collapsed Header");

    await user.click(screen.getByRole("button"));
    await rerender({ modelValue: true });

    expect(screen.getByRole("button")).toHaveTextContent("Expanded Header");
  });

  it("updates v-model when accordion is closed", async () => {
    const user = userEvent.setup();
    const { rerender } = await renderSuspended(SingleAccordion, {
      props: {
        modelValue: true,
        headerCollapsed: "Collapsed Header",
        headerExpanded: "Expanded Header",
      },
    });

    expect(screen.getByRole("button")).toHaveTextContent("Expanded Header");

    await user.click(screen.getByRole("button"));
    await rerender({ modelValue: false });

    expect(screen.getByRole("button")).toHaveTextContent("Collapsed Header");
  });

  it("responds to external v-model changes", async () => {
    const { rerender } = await renderSuspended(SingleAccordion, {
      props: {
        modelValue: false,
        headerCollapsed: "Collapsed Header",
        headerExpanded: "Expanded Header",
      },
    });

    expect(screen.getByRole("button")).toHaveTextContent("Collapsed Header");

    await rerender({ modelValue: true });
    expect(screen.getByRole("button")).toHaveTextContent("Expanded Header");

    await rerender({ modelValue: false });
    expect(screen.getByRole("button")).toHaveTextContent("Collapsed Header");
  });
});
