import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppHeader from "./Header.vue";

describe("Header", () => {
  it("renders correctly", async () => {
    await renderSuspended(AppHeader);
    expect(
      screen.getByRole("navigation", { name: "Servicemenü" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("navigation", { name: "Hauptmenü" }),
    ).toBeInTheDocument();
  });

  it("toggles the mobile menu", async () => {
    const user = userEvent.setup();
    await renderSuspended(AppHeader);

    const menuBtn = screen.getByRole("button", { name: "Menü" });

    await user.click(menuBtn);
    expect(menuBtn).toHaveAttribute("aria-expanded", "true");
    expect(screen.getByTestId("mobile-main-menu")).toBeVisible();
    expect(screen.getByTestId("mobile-service-menu")).toBeVisible();

    await user.click(menuBtn);
    expect(menuBtn).toHaveAttribute("aria-expanded", "false");
    expect(screen.getByTestId("mobile-main-menu")).not.toBeVisible();
    expect(screen.getByTestId("mobile-service-menu")).not.toBeVisible();
  });
});
