import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppBanner from "./AppBanner.vue";

describe("AppBanner", () => {
  it("shows the trial banner", async () => {
    await renderSuspended(AppBanner);
    expect(screen.getByRole("status")).toBeInTheDocument();
    expect(
      screen.getByRole("link", {
        name: /Erfahren Sie mehr über die Testphase/,
      }),
    ).toBeInTheDocument();
  });
});
