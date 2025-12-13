import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import MobileNav from "./MobileNav.vue";

describe("MobileNav", () => {
  it("renders header with navigation", async () => {
    await renderSuspended(MobileNav);
    expect(screen.getByRole("banner")).toBeInTheDocument();
    expect(screen.getByText(/This is a trial service/i)).toBeInTheDocument();
  });
});
