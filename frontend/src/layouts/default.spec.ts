import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import DefaultLayout from "./default.vue";

describe("default", () => {
  it("renders slot content", async () => {
    await renderSuspended(DefaultLayout, {
      slots: { default: "<p>Page content</p>" },
    });
    expect(screen.getByText("Page content")).toBeInTheDocument();
  });
});
