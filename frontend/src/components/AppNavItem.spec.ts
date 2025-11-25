import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppNavItem from "./AppNavItem.vue";

describe("AppNavItem", () => {
  it("renders a link", async () => {
    await renderSuspended(AppNavItem, {
      props: { item: { text: "Start", link: "/" } },
    });
    const link = screen.getByRole("link", { name: "Start" });
    expect(link).toHaveAttribute("href", expect.stringContaining("/"));
  });
});
