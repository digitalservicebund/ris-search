import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import NavbarTop from "./NavbarTop.vue";

describe("NavbarTop", () => {
  it("shows main navigation with search link", async () => {
    await renderSuspended(NavbarTop);
    expect(
      screen.getByRole("navigation", { name: "Hauptnavigation" }),
    ).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Suche" })).toBeInTheDocument();
  });
});
