import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppHeader from "./AppHeader.vue";

describe("AppHeader", () => {
  it("toggles the mobile menu", async () => {
    const user = userEvent.setup();
    await renderSuspended(AppHeader);

    const menuBtn = screen.getByRole("button", { name: "Menü" });

    await user.click(menuBtn);
    expect(menuBtn).toHaveAttribute("aria-expanded", "true");
    expect(screen.getByTestId("mobile-nav")).toBeVisible();

    await user.click(menuBtn);
    expect(menuBtn).toHaveAttribute("aria-expanded", "false");
    expect(screen.getByTestId("mobile-nav")).not.toBeVisible();
  });
});
