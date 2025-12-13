import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Sidebar from "./Sidebar.vue";

describe("Sidebar", () => {
  it("shows logo and menu", async () => {
    await renderSuspended(Sidebar);
    expect(
      screen.getByRole("link", { name: "Zur Startseite" }),
    ).toBeInTheDocument();
    expect(screen.getByRole("navigation")).toBeInTheDocument();
  });
});
