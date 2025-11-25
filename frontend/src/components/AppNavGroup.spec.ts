import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppNavGroup from "./AppNavGroup.vue";

describe("AppNavGroup", () => {
  it("renders a navigation item", async () => {
    await renderSuspended(AppNavGroup, {
      props: { item: { text: "Start", link: "/" } },
    });
    const link = screen.getByRole("link", { name: "Start" });
    expect(link).toHaveAttribute("href", expect.stringContaining("/"));
  });
});
