import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import AppBanner from "./AppBanner.vue";

describe("AppBanner", () => {
  it("shows the trial banner", async () => {
    await renderSuspended(AppBanner);
    expect(screen.getByRole("alert")).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: /Finden Sie heraus, was das bedeutet/ }),
    ).toBeInTheDocument();
  });
});
