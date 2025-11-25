import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppNav from "./AppNav.vue";

describe("AppNav", () => {
  it("shows the main links", async () => {
    await renderSuspended(AppNav);
    expect(screen.getByRole("link", { name: "Suche" })).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: "Gesetze & Verordnungen" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: "Rechtsprechung" }),
    ).toBeInTheDocument();
  });
});
