import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import BaseLayout from "./base.vue";

describe("BaseLayout", () => {
  it("renders slot content", async () => {
    await renderSuspended(BaseLayout, {
      slots: { default: "<p>Page content</p>" },
    });
    expect(screen.getByText("Page content")).toBeInTheDocument();
  });
});
