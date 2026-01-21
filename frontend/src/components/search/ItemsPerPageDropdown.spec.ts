import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import ItemsPerPageDropdown from "./ItemsPerPageDropdown.vue";

describe("items per page dropdown", () => {
  it("emits update when items per page changes", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(ItemsPerPageDropdown, {
      props: { modelValue: 10 },
    });

    await user.click(screen.getByRole("combobox"));
    await user.click(screen.getByText("50"));

    expect(emitted("update:modelValue")).toContainEqual([50]);
  });

  it("displays the passed value", async () => {
    await renderSuspended(ItemsPerPageDropdown, {
      props: { modelValue: 50 },
    });

    expect(screen.getByRole("combobox")).toHaveTextContent("50");
  });

  it("displays custom values in options", async () => {
    const user = userEvent.setup();

    await renderSuspended(ItemsPerPageDropdown, {
      props: { modelValue: 42 },
    });

    expect(screen.getByRole("combobox")).toHaveTextContent("42");

    await user.click(screen.getByRole("combobox"));

    expect(screen.getAllByText("42").length).toBeGreaterThanOrEqual(1);
    expect(screen.getByText("10")).toBeInTheDocument();
    expect(screen.getByText("50")).toBeInTheDocument();
    expect(screen.getByText("100")).toBeInTheDocument();
  });
});
