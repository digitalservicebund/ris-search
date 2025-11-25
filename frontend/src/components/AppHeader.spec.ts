import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppHeader from "./AppHeader.vue";

describe("AppHeader", () => {
  it("toggles the mobile menu", async () => {
    const user = userEvent.setup();
    await renderSuspended(AppHeader);

    const openBtn = screen.getByRole("button", { name: /Menu/ });
    await user.click(openBtn);
    expect(screen.getByRole("button", { name: /Menu/ })).toHaveAttribute(
      "id",
      "mobile-menu-close",
    );

    const closeBtn = screen.getByRole("button", { name: /Menu/ });
    await user.click(closeBtn);
    expect(screen.getByRole("button", { name: /Menu/ })).toHaveAttribute(
      "id",
      "mobile-menu",
    );
  });
});
