import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import PopoverNavigation from "./PopoverNavigation.vue";

describe("PopoverNavigation", () => {
  it("opens and closes the menu", async () => {
    await renderSuspended(PopoverNavigation);
    const user = userEvent.setup();

    const openButton = screen.getByRole("button");
    expect(screen.queryByText(/Menu/)).toBeInTheDocument();

    await user.click(openButton);
    expect(screen.getByRole("button")).toBeInTheDocument();
    expect(screen.getByRole("navigation")).toBeInTheDocument();

    await user.click(screen.getByRole("button"));
  });
});
